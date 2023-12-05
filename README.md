# CM1007-journal-accountService

# Containerisation
## Creating joint network 
docker network create journalNet

## Creating database
docker run -d --name journal-db --network=journalNet -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=journalDB -p 3307:3306 mysql:latest

## Building and running image
docker build -t account_service .
docker run -p 8081:8081 --net journalNet -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=accountDB -e MYSQL_USER=root -e MYSQL_PASSWORD=password -e MYSQL_URL=jdbc:mysql://journal-db/accountDB -e JOURNAL_SERVICE_URL=http://journal_service_con:8080 --name account_service_con account_service
JOURNAL_SERVICE_URL: http://journal_service:8080
