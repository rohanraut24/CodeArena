package codeArena;

import org.springframework.boot.SpringApplication;

public class TestCodeArenaApplication {

	public static void main(String[] args) {
		SpringApplication.from(CodeArenaApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
