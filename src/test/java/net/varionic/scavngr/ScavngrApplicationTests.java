package net.varionic.scavngr;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScavngrApplicationTests {
	@Rule
	public Snapshot snapshot = new Snapshot();

	@Test
	public void contextLoads() {

	}
}
