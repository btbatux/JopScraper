package dev.btbatux.jobscraper.repository;
import dev.btbatux.jobscraper.model.JobListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobListingRepository extends JpaRepository<JobListing, Long> {

}
