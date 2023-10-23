@echo off
REM This script sets up the AWS resources needed for the exercises. It should be
REM executed before starting the exercises. It requires the AWS CLI to be
REM installed and configured. It also requires the localstack container to be
REM running.

REM The endpoint is the address of the localstack container.
set ENDPOINT=http://localhost:4566
REM The region is not important for localstack, but we need to specify it anyway.
set REGION=us-east-1

REM Create Bucket
aws s3api create-bucket ^
    --bucket images ^
    --region %REGION% ^
    --endpoint-url %ENDPOINT%

REM Create DB
aws dynamodb create-table ^
    --table-name users ^
    --key-schema AttributeName=id,KeyType=HASH ^
    --attribute-definitions AttributeName=id,AttributeType=S ^
    --billing-mode PAY_PER_REQUEST ^
    --region %REGION% ^
    --endpoint-url %ENDPOINT%

aws dynamodb put-item ^
    --table-name users ^
    --item "{ ""id"": {""S"": ""1""}, ""displayName"": {""S"": ""Elijah""}, ""hasPostedToday"": {""BOOL"": true} }" ^
    --endpoint-url %ENDPOINT% ^
    --region %REGION%

aws dynamodb put-item ^
    --table-name users ^
    --item "{ ""id"": {""S"": ""2""}, ""displayName"": {""S"": ""Jule""}, ""hasPostedToday"": {""BOOL"": true} }" ^
    --endpoint-url %ENDPOINT% ^
    --region %REGION%

aws dynamodb put-item ^
    --table-name users ^
    --item "{ ""id"": {""S"": ""student""}, ""displayName"": {""S"": ""Student (You)""}, ""hasPostedToday"": {""BOOL"": true} }" ^
    --endpoint-url %ENDPOINT% ^
    --region %REGION%

REM Add the images to the bucket
aws s3api put-object ^
    --bucket images ^
    --key unblurred_images/1.jpg ^
    --endpoint-url %ENDPOINT% ^
    --body src/main/resources/images/user1.jpg

aws s3api put-object ^
    --bucket images ^
    --key unblurred_images/1.jpg ^
    --endpoint-url %ENDPOINT% ^
    --body src/main/resources/images/user1-yesterday.jpg

aws s3api put-object ^
    --bucket images ^
    --key unblurred_images/2.jpg ^
    --endpoint-url %ENDPOINT% ^
    --body src/main/resources/images/user2.jpg

aws s3api put-object ^
    --bucket images ^
    --key unblurred_images/2.jpg ^
    --endpoint-url %ENDPOINT% ^
    --body src/main/resources/images/user2-yesterday.jpg

aws s3api put-object ^
    --bucket images ^
    --key unblurred_images/student.jpg ^
    --endpoint-url %ENDPOINT% ^
    --body src/main/resources/images/student-yesterday.jpg

aws s3api put-object ^
    --bucket images ^
    --key blurred_images/1.jpg ^
    --endpoint-url %ENDPOINT% ^
    --body src/main/resources/images/blurred-user1.jpg

aws s3api put-object ^
    --bucket images ^
    --key blurred_images/2.jpg ^
    --endpoint-url %ENDPOINT% ^
    --body src/main/resources/images/blurred-user2.jpg

echo Setup complete! Good luck with the exercises!