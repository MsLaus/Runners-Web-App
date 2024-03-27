package com.mslaus.runners.run;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mslaus.runners.RunnersApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class RunJSONDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RunnersApplication.class);
    private final JdbcClientRunRepository runRepository;
    private ObjectMapper objectMapper;

    public RunJSONDataLoader(JdbcClientRunRepository runRepository, ObjectMapper objectMapper){
        this.runRepository = runRepository;
        this.objectMapper = objectMapper;
    }
    @Override
    public void run(String... args) throws Exception {

        if(runRepository.count() == 0){

            //reading json file
            try(InputStream inputStream = TypeReference.class.getResourceAsStream("/data/runs.json")){

                //inserting object into the list
                Runs allRuns = objectMapper.readValue(inputStream, Runs.class);
                log.info("Reading {} runs from JSON data and saving to in-memory collection.", allRuns.runs().size());

                //saving the runs in the db
                runRepository.saveAll(allRuns.runs());

            }catch (IOException e){
                throw new RuntimeException("Failed to read JSON.", e);
            }
        }else log.info("Not loading Runs from JSON data because the collection contains data.");
    }
}
