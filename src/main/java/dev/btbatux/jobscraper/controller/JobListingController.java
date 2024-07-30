package dev.btbatux.jobscraper.controller;

import dev.btbatux.jobscraper.model.JobListing;
import dev.btbatux.jobscraper.service.JobListingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobListingController {

    private final JobListingService jobListingService;


    public JobListingController(JobListingService jobListingService) {
        this.jobListingService = jobListingService;

    }

    @GetMapping
    public List<JobListing> getAllJobs() {
        return jobListingService.getAllJobListings();
    }

}
