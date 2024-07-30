package dev.btbatux.jobscraper.service;

import dev.btbatux.jobscraper.ScrapeJob;
import dev.btbatux.jobscraper.model.JobListing;
import dev.btbatux.jobscraper.repository.JobListingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobListingService {

    private final JobListingRepository jobListingRepository;


    public JobListingService(JobListingRepository jobListingRepository ) {
        this.jobListingRepository = jobListingRepository;

    }

    public List<JobListing> getAllJobListings() {
        return jobListingRepository.findAll();
    }



}
