package com.yas.location.viewmodel.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void constructor_threeArgs_createsWithEmptyFieldErrors() {
        ErrorVm vm = new ErrorVm("404", "Not Found", "Resource not found");
        assertEquals("404", vm.statusCode());
        assertEquals("Not Found", vm.title());
        assertEquals("Resource not found", vm.detail());
        assertNotNull(vm.fieldErrors());
        assertEquals(0, vm.fieldErrors().size());
    }

    @Test
    void constructor_fourArgs_createsWithFieldErrors() {
        List<String> errors = List.of("field1 is required", "field2 invalid");
        ErrorVm vm = new ErrorVm("400", "Bad Request", "Validation failed", errors);
        assertEquals("400", vm.statusCode());
        assertEquals("Bad Request", vm.title());
        assertEquals("Validation failed", vm.detail());
        assertEquals(2, vm.fieldErrors().size());
        assertEquals("field1 is required", vm.fieldErrors().get(0));
        assertEquals("field2 invalid", vm.fieldErrors().get(1));
    }
}
