package com.bm.droolengine;

import com.bm.droolengine.entity.Rule;
import com.bm.droolengine.model.EvaluationCriteria;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@Slf4j
public class DroolengineApplication {

    private static final ObjectDataCompiler objectDataCompiler = new ObjectDataCompiler();

    private static final KieHelper kieHelper = new KieHelper();

    public static void main(String[] args) {
        SpringApplication.run(DroolengineApplication.class, args);

        droolsAction();
    }

    public static void droolsAction() {
        /**
         * Get DB Rules
         */
        List<Rule> rules = getDBRules();
        log.info("rules {} ", rules);

        /**
         * Compile rule based on template
         */
        String compiledRules = getCompiledRules(rules);
        log.info("compiled rules {} ", compiledRules);

        /**
         * Create session from compiled rules
         */
        KieSession kieSession = createSession(compiledRules);
        log.info("kieSession {} ", kieSession);

        /**
         * Evaluate Fact
         */
        EvaluationCriteria evaluationCriteria = EvaluationCriteria.builder().
                sellerId("32cea80f6db8-b5ee-4bf6-3f1c-c0069f22").department(4005.0).purchagedLocation("Bombay").build();
        FactHandle request = kieSession.insert(evaluationCriteria);
        log.info("Fire Rules against fact and match count is {}", kieSession.fireAllRules());
        kieSession.delete(request);

        /**
         * Print Action
         */
        log.info("Evaluated Action {}", evaluationCriteria.getAction());
    }

    private static KieSession createSession(String compiledRules) {
        kieHelper.addContent(compiledRules, ResourceType.DRL);
        return kieHelper.build().newKieSession();
    }

    private static String getCompiledRules(List<Rule> rules) {
        return objectDataCompiler.compile(rules,
                Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/ruleengine.drl")
        );
    }

    private static List<Rule> getDBRules() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRules = Files.readString(Paths.get(ResourceUtils.getFile("classpath:static/rules.json").getPath()), StandardCharsets.UTF_8);
            Rule[] rules = objectMapper.readValue(jsonRules, Rule[].class);
            return Arrays.asList(rules);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Collections.emptyList();
    }

}
