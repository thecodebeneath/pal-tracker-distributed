package io.pivotal.pal.tracker.timesheets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import java.util.HashMap;
import java.util.Map;

public class ProjectClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    Map<Long, ProjectInfo> localProjects = new HashMap<>();
    private final RestOperations restOperations;
    private final String endpoint;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo projectInfo = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        localProjects.put(projectId, projectInfo);
        return projectInfo;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache", projectId);

        ProjectInfo projectInfo = null;

        if (localProjects.isEmpty()) {
            logger.info("Default circuit builder case goes here...");
//            return projectInfoBuilder()
//                    .id(record.id)
//                    .accountId(record.accountId)
//                    .name(record.name)
//                    .active(record.active)
//                    .info("project info")
//                    .build();
            projectInfo = null;
        } else {
            projectInfo = localProjects.get(projectId);
        }
        return projectInfo;

    }

//    public static Builder projectInfoBuilder() {
//        return new Builder();
//    }
}
