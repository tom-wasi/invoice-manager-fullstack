package invoicemanagerv2.s3;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;

public class FakeS3 implements S3Client {

    private static final String PATH =
            System.getProperty("user.home") + "/.invoice-manager/s3";

    @Override
    public String serviceName() {
        return "fake";
    }

    @Override
    public void close() {
    }

    @Override
    public PutObjectResponse putObject(PutObjectRequest putObjectRequest,
                                       RequestBody requestBody)
            throws AwsServiceException, SdkClientException {
        InputStream inputStream = requestBody.contentStreamProvider().newStream();

        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            FileUtils.writeByteArrayToFile(
                    new File(
                            buildObjectFullPath(
                                    putObjectRequest.bucket(),
                                    putObjectRequest.key())
                    ),
                    bytes
            );
            return PutObjectResponse.builder().build();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public ResponseInputStream<GetObjectResponse> getObject(
            GetObjectRequest getObjectRequest)
            throws AwsServiceException, SdkClientException {

        try {
            FileInputStream fileInputStream = new FileInputStream(
                    buildObjectFullPath(
                            getObjectRequest.bucket(),
                            getObjectRequest.key())
            );
            return new ResponseInputStream<>(
                    GetObjectResponse.builder().build(),
                    fileInputStream
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public DeleteObjectResponse deleteObject(DeleteObjectRequest deleteObjectRequest)
            throws AwsServiceException, SdkClientException {
        File file = new File(
                buildObjectFullPath(
                        deleteObjectRequest.bucket(),
                        deleteObjectRequest.key())
        );
        if (file.exists()) {
            if (file.delete()) {
                return DeleteObjectResponse.builder().build();
            } else {
                throw new RuntimeException("Failed to delete file");
            }
        } else {
            throw new RuntimeException("File not found");
        }
    }


    private String buildObjectFullPath(String bucketName, String key) {
        return PATH + "/" + bucketName + "/" + key;
    }
}
