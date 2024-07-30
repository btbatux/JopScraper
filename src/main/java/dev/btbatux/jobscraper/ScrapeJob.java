package dev.btbatux.jobscraper;

import dev.btbatux.jobscraper.model.JobListing;
import dev.btbatux.jobscraper.repository.JobListingRepository;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ScrapeJob {

    @Value("${webhook.url}")
    private String webhookUrl;
    @Value("${baseUrl}")
    private String baseUrl;
    @Value("${domain}")
    private String domain;
    @Value("${keywords}")
    private String[] keywords;
    @Value("${base.page.number}")
    private int pageNumber;

    private final JobListingRepository jobListingRepository;

    public ScrapeJob(JobListingRepository jobListingRepository) {
        this.jobListingRepository = jobListingRepository;
    }

    @PostConstruct
    public void onStartup() {
        scrapeJobListings();
    }


    @Scheduled(cron = "0 0/30 * * * ?")
    public void scrapeJobListings() {
        boolean morePages = true;
        List<JobListing> jobListingsToSave = new ArrayList<>();

        while (morePages) {
            try {
                String currentPageUrl = baseUrl + pageNumber;
                Document doc = Jsoup.connect(currentPageUrl).get();
                Element listItemsWrapper = doc.selectFirst(".list-items-wrapper");

                if (listItemsWrapper != null) {
                    Elements jobListings = listItemsWrapper.select(".list-items");

                    for (Element jobListing : jobListings) {
                        String jobTitle = jobListing.text().toLowerCase();
                        // Her anahtar kelime için kontrol
                        boolean containsKeyword = false;

                        for (String keyword : keywords) {
                            if (jobTitle.contains(keyword.toLowerCase())) {
                                containsKeyword = true;
                                break;
                            }
                        }

                        if (containsKeyword) {
                            // İlanın tam linkini al
                            String jobLink = domain + jobListing.selectFirst("a").attr("href");
                            JobListing jobListingEntity = new JobListing();
                            jobListingEntity.setTitle(jobTitle);
                            jobListingEntity.setLink(jobLink);

                            jobListingsToSave.add(jobListingEntity);
                            jobListingRepository.save(jobListingEntity);

                            System.out.println("İlan Başlığı: " + jobTitle);
                            System.out.println("İlan Linki: " + jobLink);
                        }
                    }
                    // Sonraki sayfaya geç
                    pageNumber++;
                } else {
                    // İlanlar bulunamadıysa, döngüyü sonlandır
                    morePages = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Hata durumunda döngüyü sonlandır
                morePages = false;
            }
        }
        if (!jobListingsToSave.isEmpty()) {
            sendToDiscord(jobListingsToSave);
        }
    }


    private void sendToDiscord(List<JobListing> jobListings) {
        HttpClient client = HttpClient.newHttpClient();


        for (JobListing jobListing : jobListings) {

            String jsonPayload = String.format("{\"content\": \"Yeni iş ilanı: %s\\nLink: %s\"}", jobListing.getTitle(), jobListing.getLink());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 204) {
                            System.out.println("Mesaj başarıyla gönderildi.");
                        } else {
                            System.out.println("Mesaj gönderilirken bir hata oluştu. Status Code: " + response.statusCode());
                        }
                    })
                    .exceptionally(ex -> {
                        System.out.println("Mesaj gönderilirken bir hata oluştu: " + ex.getMessage());
                        return null;
                    });
        }
    }
}
