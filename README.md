# aws-app-demo

1. Show AWS access locally `./mvnw quarkus:dev`
   - Superuser credentials (works)
   - Secured user credentials (doesn't work)
   - Add STS code with secured user (works again)
2. Show ROSA STS with web identity token
   - Remove code
   - Deploy the app `./mvnw package -Dquarkus.kubernetes.deploy=true`
   - Add service account `serviceAccountName: sts-service-account`
   - Add env variable: `AWS_REGION = us-east-2`
   - Show it works
   - Console to the pod, show env variables
3. Show Secrets Manager