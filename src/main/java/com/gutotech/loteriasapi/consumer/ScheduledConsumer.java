package com.gutotech.loteriasapi.consumer;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledConsumer {
	
	private static final Logger LOG = LoggerFactory.getLogger(ScheduledConsumer.class);

	@Autowired
	private LoteriasUpdate loteriasUpdate;
	
	@Scheduled(cron = "0 0 12 * * MON-SAT", zone = "America/Sao_Paulo")
	public void checkForNewResults12() {
		LOG.info("The time is now " + Instant.now());
		checkForUpdates();
	}

	@Scheduled(cron = "0 0 21 * * MON-SAT", zone = "America/Sao_Paulo")
	public void checkForNewResults21() {
		LOG.info("The time is now " + Instant.now());
		checkForUpdates();
	}

	@Scheduled(cron = "0 15 21 * * MON-SAT", zone = "America/Sao_Paulo")
	public void checkForNewResults2115() {
		LOG.info("The time is now " + Instant.now());
		checkForUpdates();
	}

	@Scheduled(cron = "0 0 22 * * MON-SAT", zone = "America/Sao_Paulo")
	public void checkForNewResults22() {
		LOG.info("The time is now " + Instant.now());
		checkForUpdates();
	}

	@Scheduled(cron = "0 20 0 * * MON-SAT", zone = "America/Sao_Paulo")
	public void checkForNewResults0020() {
		LOG.info("The time is now " + Instant.now());
		checkForUpdates();
	}
	
	@Scheduled(cron = "0 0 1 * * MON-SAT", zone = "America/Sao_Paulo")
	public void checkForNewResults01() {
		LOG.info("The time is now " + Instant.now());
		checkForUpdates();
	}

	public void checkForUpdates() {
		try {
			loteriasUpdate.checkForUpdates();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Scheduled(cron = "0 0/15 * * * ?", zone = "America/Sao_Paulo")
	public void keepServiceUp() {
		LOG.info("KeepServiceUp. The time is now " + Instant.now());
		loteriasUpdate.keepServiceUp();
	}
}
