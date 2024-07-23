# Investment Funds

## Introduction
The `investment-funds` project is designed to facilitate access to and management of Hungarian investment fund data. It operates by processing CSV files obtained from the bamosz.hu website, transforming this data into a structured format, and making it accessible through a custom-built REST API. This approach aims to provide an easy and efficient way to access Hungarian funds data for analysis and integration into financial applications.

## Technologies Used
- **Kotlin & Java**: For backend logic and data manipulation.
- **Spring Boot**: For creating the web application and RESTful services.
- **Maven**: For dependency management and project build.
- **Google Apps Script**: For the Google Sheets extension.

## Repository Structure
- `sheets-extension/`: Contains Google Apps Script files for the Google Sheets extension.
- `src/main/kotlin/com/github/pozo/investmentfunds/`: Kotlin and Java source files for backend services.
  - `api/`: Contains REST API endpoints that serve the processed funds data to clients.
  - `service/`: Implements the business logic, including the processing of CSV files from bamosz.hu, data transformation, and management.
  - `model/`: Defines the data models representing the structure of funds data.
  - `repository/`: Handles data persistence, allowing for efficient storage and retrieval of fund information.
  - `util/`: Includes utility classes for CSV parsing, data conversion, and other common tasks.

## Data Grabbing and Processing
The `api/grabber` package within the project plays a crucial role in ensuring the application's data is up-to-date and accurate. It is responsible for fetching, parsing, and processing investment fund data from external sources, particularly CSV files from the bamosz.hu website. This package includes several key components:

- **GrabberService**: Orchestrates the data grabbing process, including downloading, parsing, transforming, and updating the database with new data.
- **CsvDownloader**: Downloads CSV files from the bamosz.hu website.
- **CsvParser**: Parses the downloaded CSV files into a structured format.
- **DataTransformer**: Transforms parsed data into the application's domain models.
- **DataUpdater**: Updates the application's database with the new or updated data.

This structured approach ensures modularity, ease of maintenance, and clear separation of concerns, crucial for the scalability and reliability of the application.

## Terraform Configuration for AWS and GitHub Integration

This project leverages Terraform to automate the setup and management of AWS resources and integrates with GitHub for CI/CD workflows. Below is an overview of the key Terraform configuration files and their roles in the project.

### AWS ECS (`ecs.tf`)

- **AWS ECS Cluster**: Hosts the containerized services.
- **IAM Roles**: Includes `investmentfunds-role` for EC2 instances and `investmentfunds-ecs-role` for ECS tasks.
- **ECS Task Definition**: Defines the application and Redis containers, including CPU, memory, and environment variables.
- **ECS Service**: Manages the deployment of the task definition to the ECS cluster.
- **Networking**: Configures VPC, subnets, and security groups to support the ECS service.
- **Load Balancer**: Distributes incoming traffic across ECS tasks and manages HTTP to HTTPS redirection.

### AWS ECR (`ecr.tf`)

- **AWS ECR Repository**: Named `investmentfunds/api`, stores Docker images for the API, supports mutable image tags, and enables image scanning on push.

### GitHub Integration (`github.tf`)

- **GitHub Actions Secrets**: Stores AWS credentials and configurations as secrets in the GitHub repository for CI/CD workflows.
- **GitHub Actions Variables**: Defines variables like ECS cluster name, task definition family, and service name for use in GitHub Actions workflows.

### IAM Roles for GitHub Actions (`github-roles.tf`)

- **IAM Roles**: `github-build-actions-role` for building and pushing Docker images, and `github-deploy-actions-role` for deploying images to ECS.
- **Policies**: Define permissions for interacting with ECR and ECS, including pushing images and updating services.

### Terraform Configuration (`main.tf`)

- **Version and Providers**: Specifies the minimum Terraform version and configures the AWS and GitHub providers.
- **Provider Configuration**: Sets up the AWS region and GitHub token for Terraform operations.

This configuration ensures a seamless integration between AWS services for hosting the application and GitHub for continuous integration and deployment, aligning with best practices for cloud-native application development.

## GitHub Workflows

The project utilizes GitHub Actions for Continuous Integration (CI) and Continuous Deployment (CD) to automate the testing and deployment process. There are two main workflows defined in `.github/workflows/`:

### Deploy Workflow (`deploy.yaml`)

Triggered on pull requests to the `main` branch or direct pushes to `main`, this workflow automates the deployment of the investment funds API to AWS ECS. It includes steps for checking out the repository, configuring AWS credentials, building and pushing the Docker image to Amazon ECR, and updating the ECS service with the new image.

### PR Workflow (`pr.yaml`)

Triggered by pull requests to the `main` branch, this workflow focuses on building the Docker image for the API. It serves as a validation step to ensure that the Docker image can be successfully built from the PR's codebase.

These workflows ensure that new changes are automatically tested and deployed, maintaining the reliability and stability of the application.

### Google Sheets Extension
- Add the `.gs` files from `sheets-extension/` to a Google Sheets project via the Script Editor.
- Use custom functions like `FUND_RATE_LOOKUP()` directly in your sheets.

### AppScript Development Environment Setup
To develop and test the Google Sheets extension scripts (`sheets-extension/`), you'll need to set up the Google Apps Script development environment. Follow these steps to get started:

1. **Open Google Sheets**: Create a new Google Sheets document or open an existing one where you want to use the extension.

2. **Access Script Editor**: From the Google Sheets menu, navigate to `Extensions > Apps Script`. This will open the Google Apps Script editor in a new tab.

3. **Setup Project**: In the Apps Script editor, you can write your code directly or paste the `.gs` files from the `sheets-extension/` directory. The editor provides a basic IDE-like environment with syntax highlighting and code completion for Apps Script development.

4. **Enable Google Apps Script API**: Visit the [Google Cloud Platform Console](https://console.cloud.google.com/), select or create a project, and make sure the Google Apps Script API is enabled for your project. This step is crucial for programmatically managing scripts and deploying them.

5. **Test Your Script**: Use the `Run` button in the Apps Script editor to test your script. You may need to authorize the script to run under your Google account the first time.