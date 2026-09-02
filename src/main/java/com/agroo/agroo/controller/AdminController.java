package com.agroo.agroo.controller;

import com.agroo.agroo.dto.request.AlertRequest;
import com.agroo.agroo.dto.request.PriceRequest;
import com.agroo.agroo.dto.response.AlertResponse;
import com.agroo.agroo.dto.response.DashboardStats;
import com.agroo.agroo.dto.response.PriceResponse;
import com.agroo.agroo.model.ActivityLog;
import com.agroo.agroo.model.User;
import com.agroo.agroo.model.enums.ActivityType;
import com.agroo.agroo.model.enums.AlertType;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final AdminService adminService;
    private final DashboardService dashboardService;
    private final PriceService priceService;
    private final AlertService alertService;

    // ============================================================
    // DASHBOARD
    // ============================================================
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    // ============================================================
    // USER MANAGEMENT
    // ============================================================
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @GetMapping("/users/all")
    public ResponseEntity<List<User>> getAllUsersList() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.searchUsers(keyword, pageable));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUser(userId));
    }

    @PatchMapping("/users/{userId}/activate")
    public ResponseEntity<Map<String, Object>> activateUser(@PathVariable Long userId) {
        User user = adminService.activateUser(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User activated successfully");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable Long userId) {
        User user = adminService.deactivateUser(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User deactivated successfully");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/make-admin")
    public ResponseEntity<Map<String, Object>> makeAdmin(@PathVariable Long userId) {
        User user = adminService.makeAdmin(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User is now an admin");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/{userId}/remove-admin")
    public ResponseEntity<Map<String, Object>> removeAdmin(@PathVariable Long userId) {
        User user = adminService.removeAdmin(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin privileges removed");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // PRICE MANAGEMENT
    // ============================================================
    @PostMapping("/prices")
    public ResponseEntity<PriceResponse> addPrice(
            @Valid @RequestBody PriceRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(priceService.addPrice(request, username));
    }

    @PutMapping("/prices/{priceId}")
    public ResponseEntity<PriceResponse> updatePrice(
            @PathVariable Long priceId,
            @Valid @RequestBody PriceRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(priceService.updatePrice(priceId, request, username));
    }

    @GetMapping("/prices")
    public ResponseEntity<List<PriceResponse>> getAllPrices() {
        return ResponseEntity.ok(priceService.getAllPrices());
    }

    @GetMapping("/prices/latest")
    public ResponseEntity<List<PriceResponse>> getLatestPrices() {
        return ResponseEntity.ok(priceService.getLatestPrices());
    }

    @GetMapping("/prices/product/{productName}")
    public ResponseEntity<List<PriceResponse>> getPricesByProduct(@PathVariable String productName) {
        return ResponseEntity.ok(priceService.getPricesByProduct(productName));
    }

    @GetMapping("/prices/compare")
    public ResponseEntity<Map<String, Map<String, Double>>> comparePrices(
            @RequestParam String productName,
            @RequestParam List<String> locations) {
        return ResponseEntity.ok(priceService.comparePrices(productName, locations));
    }

    @DeleteMapping("/prices/{priceId}")
    public ResponseEntity<Map<String, String>> deletePrice(@PathVariable Long priceId) {
        priceService.deletePrice(priceId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Price deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // ALERT MANAGEMENT
    // ============================================================
    @PostMapping("/alerts")
    public ResponseEntity<AlertResponse> createAlert(
            @Valid @RequestBody AlertRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertService.createAlert(request, username));
    }

    @PutMapping("/alerts/{alertId}")
    public ResponseEntity<AlertResponse> updateAlert(
            @PathVariable Long alertId,
            @Valid @RequestBody AlertRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(alertService.updateAlert(alertId, request, username));
    }

    @GetMapping("/alerts")
    public ResponseEntity<Page<AlertResponse>> getAllAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(alertService.getAllAlerts(pageable));
    }

    @GetMapping("/alerts/active")
    public ResponseEntity<List<AlertResponse>> getActiveAlerts() {
        return ResponseEntity.ok(alertService.getActiveAlerts());
    }

    @GetMapping("/alerts/urgent")
    public ResponseEntity<List<AlertResponse>> getUrgentAlerts() {
        return ResponseEntity.ok(alertService.getUrgentAlerts());
    }

    @GetMapping("/alerts/type/{type}")
    public ResponseEntity<List<AlertResponse>> getAlertsByType(@PathVariable AlertType type) {
        return ResponseEntity.ok(alertService.getAlertsByType(type));
    }

    @PatchMapping("/alerts/{alertId}/deactivate")
    public ResponseEntity<AlertResponse> deactivateAlert(@PathVariable Long alertId) {
        return ResponseEntity.ok(alertService.deactivateAlert(alertId));
    }

    @DeleteMapping("/alerts/{alertId}")
    public ResponseEntity<Map<String, String>> deleteAlert(@PathVariable Long alertId) {
        alertService.deleteAlert(alertId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Alert deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // ADMIN ACTIONS - Manage ALL Content
    // ============================================================
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long productId) {
        adminService.deleteProduct(productId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/machines/{machineId}")
    public ResponseEntity<Map<String, String>> deleteMachine(@PathVariable Long machineId) {
        adminService.deleteMachine(machineId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Machine rental deleted successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Post deleted successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        adminService.deleteComment(commentId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Comment deleted successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<Map<String, String>> deleteGroup(@PathVariable Long groupId) {
        adminService.deleteGroup(groupId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Group deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // ACTIVITY LOGS
    // ============================================================
    @GetMapping("/logs")
    public ResponseEntity<Page<ActivityLog>> getActivityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminService.getActivityLogs(pageable));
    }

    // ============================================================
    // SYSTEM STATS
    // ============================================================
    @GetMapping("/stats/users")
    public ResponseEntity<Map<String, Long>> getUserStats() {
        return ResponseEntity.ok(adminService.getUserStats());
    }

    @GetMapping("/stats/products")
    public ResponseEntity<Map<String, Long>> getProductStats() {
        return ResponseEntity.ok(adminService.getProductStats());
    }

    @GetMapping("/stats/machines")
    public ResponseEntity<Map<String, Long>> getMachineStats() {
        return ResponseEntity.ok(adminService.getMachineStats());
    }

    @GetMapping("/stats/posts")
    public ResponseEntity<Map<String, Long>> getPostStats() {
        return ResponseEntity.ok(adminService.getPostStats());
    }

    @GetMapping("/stats/groups")
    public ResponseEntity<Map<String, Long>> getGroupStats() {
        return ResponseEntity.ok(adminService.getGroupStats());
    }
}