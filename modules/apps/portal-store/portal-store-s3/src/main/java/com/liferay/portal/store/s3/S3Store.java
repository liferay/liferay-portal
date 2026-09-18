/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.s3;

import com.liferay.document.library.kernel.exception.AccessDeniedException;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.concurrent.NoticeableThreadPoolExecutor;
import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;
import com.liferay.petra.io.unsync.UnsyncFilterInputStream;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.store.s3.configuration.S3StoreConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;

import java.time.Duration;
import java.time.Instant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.MultipartUpload;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.StorageClass;
import software.amazon.awssdk.services.s3.paginators.ListMultipartUploadsPublisher;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Publisher;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;

/**
 * @author Brian Wing Shun Chan
 * @author Sten Martinez
 * @author Edward C. Han
 * @author Vilmos Papp
 * @author Máté Thurzó
 * @author Manuel de la Peña
 * @author Daniel Sanz
 */
@Component(
	configurationPid = "com.liferay.portal.store.s3.configuration.S3StoreConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "store.type=com.liferay.portal.store.s3.S3Store",
	service = Store.class
)
public class S3Store implements Store {

	public void abortMultipartUploads(Instant startInstant) {
		try {
			List<MultipartUpload> multipartUploads = new ArrayList<>();

			ListMultipartUploadsPublisher listMultipartUploadsPublisher =
				_s3AsyncClient.listMultipartUploadsPaginator(
					listMultipartUploadsRequestBuilder ->
						listMultipartUploadsRequestBuilder.bucket(
							_s3StoreConfiguration.bucketName()));

			CompletableFuture<Void> completableFuture1 =
				listMultipartUploadsPublisher.subscribe(
					listMultipartUploadsResponse -> {
						for (MultipartUpload multipartUpload :
								listMultipartUploadsResponse.uploads()) {

							Instant initiatedInstant =
								multipartUpload.initiated();

							if (initiatedInstant.isBefore(startInstant)) {
								multipartUploads.add(multipartUpload);
							}
						}
					});

			completableFuture1.join();

			for (MultipartUpload multipartUpload : multipartUploads) {
				CompletableFuture<AbortMultipartUploadResponse>
					completableFuture2 = _s3AsyncClient.abortMultipartUpload(
						abortMultipartUploadRequestBuilder ->
							abortMultipartUploadRequestBuilder.bucket(
								_s3StoreConfiguration.bucketName()
							).key(
								multipartUpload.key()
							).uploadId(
								multipartUpload.uploadId()
							));

				completableFuture2.join();
			}
		}
		catch (CompletionException completionException) {
			throw _toSystemException(completionException.getCause());
		}
	}

	@Override
	public void addFile(
		long companyId, long repositoryId, String fileName, String versionLabel,
		InputStream inputStream) {

		try {
			if (hasFile(companyId, repositoryId, fileName, versionLabel)) {
				deleteFile(companyId, repositoryId, fileName, versionLabel);
			}

			File file = FileUtil.createTempFile(inputStream);

			String key = S3KeyTransformerUtil.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			try {
				FileUpload fileUpload = _s3TransferManager.uploadFile(
					uploadFileRequestBuilder ->
						uploadFileRequestBuilder.putObjectRequest(
							putObjectRequestBuilder ->
								putObjectRequestBuilder.bucket(
									_s3StoreConfiguration.bucketName()
								).key(
									key
								).storageClass(
									_storageClass
								)
						).source(
							file
						));

				CompletableFuture<CompletedFileUpload> completableFuture =
					fileUpload.completionFuture();

				completableFuture.join();
			}
			catch (CancellationException cancellationException) {
				if (_log.isDebugEnabled()) {
					_log.debug(cancellationException);
				}
			}
			catch (CompletionException completionException) {
				throw _toSystemException(completionException.getCause());
			}
			finally {
				FileUtil.delete(file);
			}
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	@Override
	public void deleteDirectory(long companyId) {
		_deleteObjects(S3KeyTransformerUtil.getDirectoryKey(companyId));
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		_deleteObjects(
			S3KeyTransformerUtil.getDirectoryKey(
				companyId, repositoryId, dirName));
	}

	@Override
	public void deleteFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		try {
			CompletableFuture<DeleteObjectResponse> completableFuture =
				_s3AsyncClient.deleteObject(
					deleteObjectRequestBuilder ->
						deleteObjectRequestBuilder.bucket(
							_s3StoreConfiguration.bucketName()
						).key(
							S3KeyTransformerUtil.getFileVersionKey(
								companyId, repositoryId, fileName, versionLabel)
						));

			completableFuture.join();
		}
		catch (CompletionException completionException) {
			throw _toSystemException(completionException.getCause());
		}
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		if (Validator.isNull(versionLabel)) {
			versionLabel = _getHeadVersionLabel(
				companyId, repositoryId, fileName);
		}

		try {
			String key = S3KeyTransformerUtil.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			CompletableFuture<ResponseInputStream<GetObjectResponse>>
				completableFuture = _s3AsyncClient.getObject(
					getObjectRequestBuilder -> getObjectRequestBuilder.bucket(
						_s3StoreConfiguration.bucketName()
					).key(
						key
					),
					AsyncResponseTransformer.toBlockingInputStream());

			return new UnsyncFilterInputStream(completableFuture.join());
		}
		catch (CompletionException completionException) {
			Throwable throwable = completionException.getCause();

			if (throwable instanceof NoSuchKeyException) {
				throw new NoSuchFileException(
					companyId, repositoryId, fileName, versionLabel);
			}

			throw _toSystemException(throwable);
		}
	}

	@Override
	public String[] getFileNames(
		long companyId, long repositoryId, String dirName) {

		String key = null;

		if (Validator.isNull(dirName)) {
			key = S3KeyTransformerUtil.getRepositoryKey(
				companyId, repositoryId);
		}
		else {
			key = S3KeyTransformerUtil.getDirectoryKey(
				companyId, repositoryId, dirName);
		}

		List<S3Object> s3Objects = _getS3Objects(key);

		Iterator<S3Object> iterator = s3Objects.iterator();

		String[] fileNames = new String[s3Objects.size()];

		for (int i = 0; i < fileNames.length; i++) {
			S3Object s3Object = iterator.next();

			fileNames[i] = S3KeyTransformerUtil.getFileName(s3Object.key());
		}

		return fileNames;
	}

	@Override
	public long getFileSize(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		try {
			if (Validator.isNull(versionLabel)) {
				versionLabel = _getHeadVersionLabel(
					companyId, repositoryId, fileName);
			}

			String key = S3KeyTransformerUtil.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			CompletableFuture<HeadObjectResponse> completableFuture =
				_s3AsyncClient.headObject(
					headObjectRequestBuilder -> headObjectRequestBuilder.bucket(
						_s3StoreConfiguration.bucketName()
					).key(
						key
					));

			HeadObjectResponse headObjectResponse = completableFuture.join();

			return headObjectResponse.contentLength();
		}
		catch (CompletionException completionException) {
			Throwable throwable = completionException.getCause();

			if (throwable instanceof NoSuchKeyException) {
				throw new NoSuchFileException(
					companyId, repositoryId, fileName);
			}

			throw _toSystemException(throwable);
		}
	}

	@Override
	public String[] getFileVersions(
		long companyId, long repositoryId, String fileName) {

		List<S3Object> s3Objects = _getS3Objects(
			S3KeyTransformerUtil.getFileKey(companyId, repositoryId, fileName));

		if (s3Objects.isEmpty()) {
			return StringPool.EMPTY_ARRAY;
		}

		String[] versions = new String[s3Objects.size()];

		for (int i = 0; i < s3Objects.size(); i++) {
			S3Object s3Object = s3Objects.get(i);

			String versionKey = s3Object.key();

			versions[i] = versionKey.substring(
				versionKey.lastIndexOf(CharPool.SLASH) + 1);
		}

		Arrays.sort(versions, DLUtil::compareVersions);

		return versions;
	}

	@Override
	public boolean hasFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		try {
			if (Validator.isNull(versionLabel)) {
				versionLabel = _getHeadVersionLabel(
					companyId, repositoryId, fileName);
			}

			String key = S3KeyTransformerUtil.getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);

			CompletableFuture<HeadObjectResponse> completableFuture =
				_s3AsyncClient.headObject(
					headObjectRequestBuilder -> headObjectRequestBuilder.bucket(
						_s3StoreConfiguration.bucketName()
					).key(
						key
					));

			completableFuture.join();

			return true;
		}
		catch (CompletionException completionException) {
			Throwable throwable = completionException.getCause();

			if (throwable instanceof NoSuchKeyException) {
				return false;
			}

			throw _toSystemException(throwable);
		}
		catch (NoSuchFileException noSuchFileException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileException);
			}

			return false;
		}
	}

	@Override
	public void verifyCompanyStores() {
		try {
			long[] companyIds = PortalInstancePool.getCompanyIds();
			String continuationToken = null;

			boolean hasNext = true;

			while (hasNext) {
				String currentToken = continuationToken;

				CompletableFuture<ListObjectsV2Response> completableFuture =
					_s3AsyncClient.listObjectsV2(
						builder -> builder.bucket(
							_s3StoreConfiguration.bucketName()
						).continuationToken(
							currentToken
						).delimiter(
							StringPool.SLASH
						));

				ListObjectsV2Response listObjectsV2Response =
					completableFuture.join();

				List<CommonPrefix> commonPrefixes =
					listObjectsV2Response.commonPrefixes();

				for (CommonPrefix commonPrefix : commonPrefixes) {
					String folderName = commonPrefix.prefix();

					if (folderName.endsWith(StringPool.SLASH)) {
						folderName = folderName.substring(
							0, folderName.length() - 1);
					}

					if (!Validator.isNumber(folderName)) {
						continue;
					}

					long storeCompanyId = GetterUtil.getLong(folderName);

					if (ArrayUtil.contains(companyIds, storeCompanyId)) {
						continue;
					}

					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Manually remove unused store ", storeCompanyId,
								" that belongs to company ", storeCompanyId,
								" if it is no longer used anywhere else"));
					}
				}

				if (Boolean.TRUE.equals(listObjectsV2Response.isTruncated())) {
					continuationToken =
						listObjectsV2Response.nextContinuationToken();
				}
				else {
					hasNext = false;
				}
			}
		}
		catch (CompletionException completionException) {
			throw _toSystemException(completionException.getCause());
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_s3StoreConfiguration = ConfigurableUtil.createConfigurable(
			S3StoreConfiguration.class, properties);

		AwsCredentialsProvider awsCredentialsProvider = null;

		if (Validator.isNotNull(_s3StoreConfiguration.accessKey()) &&
			Validator.isNotNull(_s3StoreConfiguration.secretKey())) {

			awsCredentialsProvider = StaticCredentialsProvider.create(
				AwsBasicCredentials.create(
					_s3StoreConfiguration.accessKey(),
					_secretResolver.resolve(
						CompanyConstants.SYSTEM,
						_s3StoreConfiguration.secretKey())));
		}
		else {
			awsCredentialsProvider = DefaultCredentialsProvider.create();
		}

		NettyNioAsyncHttpClient.Builder nettyNioAsyncHttpClientBuilder =
			NettyNioAsyncHttpClient.builder();

		nettyNioAsyncHttpClientBuilder.connectionTimeout(
			Duration.ofMillis(_s3StoreConfiguration.connectionTimeout())
		).maxConcurrency(
			_s3StoreConfiguration.httpClientMaxConnections()
		);

		String proxyHost = _s3StoreConfiguration.proxyHost();

		if (Validator.isNotNull(proxyHost)) {
			ProxyConfiguration.Builder proxyConfigurationBuilder =
				ProxyConfiguration.builder();

			proxyConfigurationBuilder.host(
				proxyHost
			).port(
				_s3StoreConfiguration.proxyPort()
			);

			String proxyAuthType = _s3StoreConfiguration.proxyAuthType();

			if (Objects.equals(proxyAuthType, "username-password")) {
				proxyConfigurationBuilder.password(
					_secretResolver.resolve(
						CompanyConstants.SYSTEM,
						_s3StoreConfiguration.proxyPassword())
				).username(
					_s3StoreConfiguration.proxyUsername()
				);
			}

			nettyNioAsyncHttpClientBuilder.proxyConfiguration(
				proxyConfigurationBuilder.build());
		}

		S3AsyncClientBuilder s3AsyncClientBuilder = S3AsyncClient.builder(
		).credentialsProvider(
			awsCredentialsProvider
		).forcePathStyle(
			_s3StoreConfiguration.s3PathStyle()
		).httpClientBuilder(
			nettyNioAsyncHttpClientBuilder
		).multipartEnabled(
			true
		).multipartConfiguration(
			multipartConfigurationBuilder ->
				multipartConfigurationBuilder.minimumPartSizeInBytes(
					(long)_s3StoreConfiguration.minimumUploadPartSize()
				).thresholdInBytes(
					(long)_s3StoreConfiguration.multipartUploadThreshold()
				)
		).overrideConfiguration(
			clientOverrideConfigurationBuilder ->
				clientOverrideConfigurationBuilder.retryPolicy(
					retryBuilder -> retryBuilder.numRetries(
						_s3StoreConfiguration.httpClientMaxErrorRetry()))
		);

		if (Validator.isNotNull(_s3StoreConfiguration.s3Endpoint())) {
			s3AsyncClientBuilder.endpointOverride(
				URI.create(_s3StoreConfiguration.s3Endpoint()));
		}

		if (Validator.isNotNull(_s3StoreConfiguration.s3Region())) {
			s3AsyncClientBuilder.region(
				Region.of(_s3StoreConfiguration.s3Region()));
		}

		_s3AsyncClient = s3AsyncClientBuilder.build();

		_threadPoolExecutor = new NoticeableThreadPoolExecutor(
			_s3StoreConfiguration.corePoolSize(),
			_s3StoreConfiguration.maxPoolSize(), 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(),
			new ThreadPoolExecutor.AbortPolicy(),
			new ThreadPoolHandlerAdapter());

		_s3TransferManager = S3TransferManager.builder(
		).executor(
			_threadPoolExecutor
		).s3Client(
			_s3AsyncClient
		).build();

		try {
			_storageClass = StorageClass.fromValue(
				_s3StoreConfiguration.s3StorageClass());
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_storageClass = StorageClass.STANDARD;

			if (_log.isWarnEnabled()) {
				_log.warn(
					_s3StoreConfiguration.s3StorageClass() +
						" is not a valid value for the storage class",
					illegalArgumentException);
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		_s3AsyncClient.close();
		_s3TransferManager.close();
		_threadPoolExecutor.shutdown();
	}

	private void _deleteObjects(String prefix) {
		try {
			List<ObjectIdentifier> objectIdentifiers = new ArrayList<>(
				_DELETE_MAX);

			List<S3Object> s3Objects = _getS3Objects(prefix);

			Iterator<S3Object> iterator = s3Objects.iterator();

			while (iterator.hasNext()) {
				for (int i = 0; i < _DELETE_MAX; i++) {
					if (iterator.hasNext()) {
						S3Object s3Object = iterator.next();

						objectIdentifiers.add(
							ObjectIdentifier.builder(
							).key(
								s3Object.key()
							).build());
					}
				}

				CompletableFuture<DeleteObjectsResponse> completableFuture =
					_s3AsyncClient.deleteObjects(
						deleteObjectsRequestBuilder ->
							deleteObjectsRequestBuilder.bucket(
								_s3StoreConfiguration.bucketName()
							).delete(
								deleteBuilder -> deleteBuilder.objects(
									objectIdentifiers)
							));

				completableFuture.join();

				objectIdentifiers.clear();
			}
		}
		catch (CompletionException completionException) {
			throw _toSystemException(completionException.getCause());
		}
	}

	private String _getHeadVersionLabel(
			long companyId, long repositoryId, String fileName)
		throws NoSuchFileException {

		List<S3Object> s3Objects = _getS3Objects(
			S3KeyTransformerUtil.getFileKey(companyId, repositoryId, fileName));

		if (s3Objects.isEmpty()) {
			throw new NoSuchFileException(companyId, repositoryId, fileName);
		}

		String key = null;

		for (S3Object s3Object : s3Objects) {
			if ((key == null) || (key.compareTo(s3Object.key()) < 0)) {
				key = s3Object.key();
			}
		}

		return key.substring(key.lastIndexOf(CharPool.SLASH) + 1);
	}

	private List<S3Object> _getS3Objects(String prefix) {
		try {
			List<S3Object> s3Objects = new ArrayList<>();

			ListObjectsV2Publisher listObjectsV2Publisher =
				_s3AsyncClient.listObjectsV2Paginator(
					listObjectsV2RequestBuilder ->
						listObjectsV2RequestBuilder.bucket(
							_s3StoreConfiguration.bucketName()
						).prefix(
							prefix
						));

			CompletableFuture<Void> completableFuture =
				listObjectsV2Publisher.subscribe(
					listObjectsV2Response -> s3Objects.addAll(
						listObjectsV2Response.contents()));

			completableFuture.join();

			return s3Objects;
		}
		catch (CompletionException completionException) {
			throw _toSystemException(completionException.getCause());
		}
	}

	private SystemException _toSystemException(Throwable throwable) {
		if (!(throwable instanceof AwsServiceException)) {
			return new SystemException(throwable.getMessage(), throwable);
		}

		StringBundler sb = new StringBundler(10);

		AwsServiceException awsServiceException =
			(AwsServiceException)throwable;

		AwsErrorDetails awsErrorDetails = awsServiceException.awsErrorDetails();

		String errorCode = awsErrorDetails.errorCode();

		if (errorCode != null) {
			sb.append("{errorCode=");
			sb.append(errorCode);
			sb.append(StringPool.COMMA_AND_SPACE);
		}
		else {
			sb.append("{");
		}

		sb.append("message=");
		sb.append(awsServiceException.getMessage());
		sb.append(", requestId=");
		sb.append(awsServiceException.requestId());
		sb.append(", statusCode=");
		sb.append(awsServiceException.statusCode());
		sb.append("}");

		if (Objects.equals(errorCode, "AccessDenied")) {
			return new AccessDeniedException(sb.toString());
		}

		return new SystemException(sb.toString());
	}

	private static final int _DELETE_MAX = 1000;

	private static final Log _log = LogFactoryUtil.getLog(S3Store.class);

	private S3AsyncClient _s3AsyncClient;
	private S3StoreConfiguration _s3StoreConfiguration;
	private S3TransferManager _s3TransferManager;

	@Reference
	private SecretResolver _secretResolver;

	private StorageClass _storageClass;
	private NoticeableThreadPoolExecutor _threadPoolExecutor;

}