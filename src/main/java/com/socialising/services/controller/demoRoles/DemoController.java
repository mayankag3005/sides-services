//package com.socialising.services.controller.demoRoles;

//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/api/v1/demo")
//@PreAuthorize("hasRole('ADMIN')")
//public class DemoController {

    /**
            ROLE - PERMISSION DEMO
     **/
//    @GetMapping
//    @PreAuthorize("hasAuthority('admin:read')")
//    public String get() {
//        return "GET::admin controller";
//    }
//
//    @PostMapping
//    @PreAuthorize("hasAuthority('admin:create')")
//    public String post() {
//        return "POST::admin controller";
//    }
//
//    @PutMapping
//    @PreAuthorize("hasAuthority('admin:update')")
//    public String put() {
//        return "PUT::admin controller";
//    }
//
//    @DeleteMapping
//    @PreAuthorize("hasAuthority('admin:delete')")
//    public String delete() {
//        return "DELETE::admin controller";
//    }

    /**
        EXCEPTION HANDLING DEMO
     **/
//    @GetMapping("/error")
//    public ResponseEntity<?> throwException() {
//        return ResponseEntity.ok(throwExceptionFn());
//    }
//
//    private ResponseEntity<?> throwExceptionFn() {
//        throw new IllegalStateException("some exception happened");
//    }
//}
