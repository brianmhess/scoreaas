# Build Docker Image

```
mvn clean package
docker build . --tag gcr.io/PROJECT-ID/scoreaas
docker push gcr.io/PROJECT-ID/scoreaas
gcloud run deploy scoreaas --image gcr.io/PROJECT-ID/scoreaas --allow-unauthenticated --platform managed --project=PROJECT-ID
```
