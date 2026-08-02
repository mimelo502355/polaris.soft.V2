package com.polaris.backend;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.epiis.app.BackendApplication;

class BackendApplicationTests {

	@Test
	void applicationEntryPointIsAvailable() throws Exception {
		Method mainMethod = BackendApplication.class.getDeclaredMethod("main", String[].class);
		assertNotNull(mainMethod);
	}

}
