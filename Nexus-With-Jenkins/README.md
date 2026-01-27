# Java Application Deployment Notes

## Maven Commands

| Step | Command | Description |
|------|---------|-------------|
| 1    | -       | Generate resources |
| 2    | `mvn compile` | Compile code |
| 3    | `mvn test` | Run unit tests |
| 4    | `mvn package` | Package application |
| 5    | `mvn install` | Install dependencies |
| 6    | `mvn clean` | Clean resources |

## Build Artifacts by Java Type

| Dev Type      | Output File Type |
|---------------|----------------|
| Java          | `.war` (Web Archive) |
| Spring Boot   | `.jar` (Java Archive) |
| J2EE          | `.ear` (Enterprise Archive) |

> **Note:** The final stage of code/package is called an **artifact**.

## Nexus Repository Creation

1. Go to `Settings → Repositories → Hosted → Create New`.  
2. Enter **Name**.  
3. Set **Deployment Policy:** `Allow Redeploy`.

## Integrate Nexus with Jenkins

1. Install **Nexus Artifact Uploader** plugin in Jenkins.  
2. Open Jenkins → Configure Build → Add **Nexus Artifact Uploader** step.  
3. Upload your artifact to the Nexus repository after a successful build.

