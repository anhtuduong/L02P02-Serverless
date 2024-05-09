./gradlew buildZip

# Deploy the function
FUNCTION_NAME=get-feed
CLASS_NAME=GetFeedFunction
aws lambda create-function \
    --function-name $FUNCTION_NAME \
    --runtime java17 \
    --zip-file fileb://build/distributions/beunreal-1.0.0.zip \
    --handler de.tum.cit.dos.eist.backend.functions.${CLASS_NAME} \
    --role arn:aws:iam::000000000000:role/lambda-role \
    --endpoint-url=http://localhost:4566 \
    --timeout 10 \
    --region us-east-1

FUNCTION_NAME=beunreal-time
CLASS_NAME=BeUnrealTimeFunction
aws lambda create-function \
    --function-name $FUNCTION_NAME \
    --runtime java17 \
    --zip-file fileb://build/distributions/beunreal-1.0.0.zip \
    --handler de.tum.cit.dos.eist.backend.functions.${CLASS_NAME} \
    --role arn:aws:iam::000000000000:role/lambda-role \
    --endpoint-url=http://localhost:4566 \
    --timeout 10 \
    --region us-east-1

FUNCTION_NAME=user-posted
CLASS_NAME=UserPostedFunction
aws lambda create-function \
    --function-name $FUNCTION_NAME \
    --runtime java17 \
    --zip-file fileb://build/distributions/beunreal-1.0.0.zip \
    --handler de.tum.cit.dos.eist.backend.functions.${CLASS_NAME} \
    --role arn:aws:iam::000000000000:role/lambda-role \
    --endpoint-url=http://localhost:4566 \
    --timeout 10 \
    --region us-east-1

# Update function
FUNCTION_NAME=get-feed
aws lambda update-function-code \
    --function-name $FUNCTION_NAME \
    --endpoint-url=http://localhost:4566 \
    --zip-file fileb://build/distributions/beunreal-1.0.0.zip \
    --region us-east-1

FUNCTION_NAME=beunreal-time
aws lambda update-function-code \
    --function-name $FUNCTION_NAME \
    --endpoint-url=http://localhost:4566 \
    --zip-file fileb://build/distributions/beunreal-1.0.0.zip \
    --region us-east-1

FUNCTION_NAME=user-posted
aws lambda update-function-code \
    --function-name $FUNCTION_NAME \
    --endpoint-url=http://localhost:4566 \
    --zip-file fileb://build/distributions/beunreal-1.0.0.zip \
    --region us-east-1

# Config S3
aws s3api put-bucket-notification-configuration \
    --bucket images \
    --endpoint-url=http://localhost:4566 \
    --notification-configuration file://s3-event-config.json \
    --region us-east-1

aws lambda add-permission \
    --function-name user-posted \
    --action lambda:InvokeFunction \
    --principal s3.amazonaws.com \
    --source-arn arn:aws:s3:::images \
    --endpoint-url=http://localhost:4566 \
    --statement-id unique-statement-id  \
    --region us-east-1