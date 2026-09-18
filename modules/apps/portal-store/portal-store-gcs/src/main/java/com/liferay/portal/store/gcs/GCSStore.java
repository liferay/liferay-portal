/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.store.gcs;

import com.google.api.gax.paging.Page;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.ReadChannel;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageBatch;
import com.google.cloud.storage.StorageBatchResult;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.store.StoreArea;
import com.liferay.document.library.kernel.store.StoreAreaProcessor;
import com.liferay.document.library.kernel.util.comparator.VersionNumberComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.store.gcs.configuration.GCSStoreConfiguration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.channels.Channels;

import java.time.Instant;
import java.time.temporal.TemporalAmount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import org.threeten.bp.Duration;

/**
 * @author Shanon Mathai
 * @author Alicia García
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.portal.store.gcs.configuration.GCSStoreConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = "store.type=com.liferay.portal.store.gcs.GCSStore",
	service = Store.class
)
public class GCSStore implements Store {

	@Override
	public void addFile(
			long companyId, long repositoryId, String fileName,
			String versionLabel, InputStream inputStream)
		throws PortalException {

		if (hasFile(companyId, repositoryId, fileName, versionLabel)) {
			deleteFile(companyId, repositoryId, fileName, versionLabel);
		}

		String path = _getFileVersionKey(
			companyId, repositoryId, fileName, versionLabel);

		BlobInfo blobInfo = BlobInfo.newBuilder(
			_getBucketInfo(), path
		).build();

		try (WriteChannel writeChannel = _getWriteChannel(blobInfo)) {
			StreamUtil.transfer(
				inputStream, Channels.newOutputStream(writeChannel));
		}
		catch (IOException ioException) {
			throw new PortalException("Unable to add file", ioException);
		}
	}

	@Override
	public void deleteDirectory(long companyId) {
		_deleteObjects(StoreArea.getCurrentStoreAreaPath(companyId));
	}

	@Override
	public void deleteDirectory(
		long companyId, long repositoryId, String dirName) {

		_deleteObjects(_getDirectoryKey(companyId, repositoryId, dirName));
	}

	@Override
	public void deleteFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		_gcsStore.delete(
			BlobId.of(
				_gcsStoreConfiguration.bucketName(),
				_getHeadVersionLabel(
					companyId, repositoryId, fileName, versionLabel)));
	}

	@Override
	public InputStream getFileAsStream(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws NoSuchFileException {

		Blob blob = _gcsStore.get(
			BlobId.of(
				_gcsStoreConfiguration.bucketName(),
				_getHeadVersionLabel(
					companyId, repositoryId, fileName, versionLabel)));

		if (blob == null) {
			throw new NoSuchFileException(
				companyId, repositoryId, fileName, versionLabel);
		}

		return Channels.newInputStream(_getReadChannel(blob));
	}

	@Override
	public String[] getFileNames(
		long companyId, long repositoryId, String dirName) {

		String prefix =
			StoreArea.getCurrentStoreAreaPath(companyId, repositoryId) +
				StringPool.SLASH;

		return TransformUtil.transform(
			_getFilePaths(companyId, repositoryId, dirName),
			filePath -> filePath.substring(
				filePath.indexOf(prefix) + prefix.length(),
				filePath.lastIndexOf(StringPool.SLASH)),
			String.class);
	}

	@Override
	public long getFileSize(
			long companyId, long repositoryId, String fileName,
			String versionLabel)
		throws PortalException {

		String headVersionLabel = _getHeadVersionLabel(
			companyId, repositoryId, fileName, versionLabel);

		Blob blob = _gcsStore.get(
			BlobId.of(_gcsStoreConfiguration.bucketName(), headVersionLabel));

		if (blob == null) {
			throw new NoSuchFileException(
				"No file exists for " + headVersionLabel);
		}

		return blob.getSize();
	}

	@Override
	public String[] getFileVersions(
		long companyId, long repositoryId, String fileName) {

		return TransformUtil.transform(
			_getFilePaths(companyId, repositoryId, fileName),
			path -> {
				String[] parts = StringUtil.split(path, CharPool.SLASH);

				return parts[parts.length - 1];
			},
			String.class);
	}

	@Override
	public boolean hasFile(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		String path = _getFileVersionKey(
			companyId, repositoryId, fileName, versionLabel);

		Page<Blob> blobPage = _gcsStore.list(
			_gcsStoreConfiguration.bucketName(),
			Storage.BlobListOption.pageSize(1),
			Storage.BlobListOption.prefix(path));

		Iterable<Blob> filesFoundIterable = blobPage.getValues();

		Iterator<Blob> filesFoundIterator = filesFoundIterable.iterator();

		return filesFoundIterator.hasNext();
	}

	@Override
	public void verifyCompanyStores() throws PortalException {
		try {
			long[] companyIds = PortalInstancePool.getCompanyIds();

			String prefix = StoreArea.getCurrentStoreAreaPath();

			Page<Blob> blobPage = _gcsStore.list(
				_gcsStoreConfiguration.bucketName(),
				Storage.BlobListOption.prefix(prefix),
				Storage.BlobListOption.currentDirectory());

			for (Blob blob : blobPage.iterateAll()) {
				String name = blob.getName();

				if (!prefix.isEmpty() && StringUtil.startsWith(name, prefix)) {
					name = StringUtil.removeSubstring(name, prefix);
				}

				if (!name.endsWith(StringPool.SLASH)) {
					continue;
				}

				String folderName = name.substring(0, name.length() - 1);

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
		}
		catch (Exception exception) {
			throw new PortalException(exception);
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceRegistration = bundleContext.registerService(
			StoreAreaProcessor.class, new GCSStoreAreaProcessor(),
			MapUtil.singletonDictionary(
				"store.type", "com.liferay.portal.store.gcs.GCSStore"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		try {
			_gcsStoreConfiguration = ConfigurableUtil.createConfigurable(
				GCSStoreConfiguration.class, properties);

			_initEncryption();

			_initGCSStore();
		}
		catch (PortalException portalException) {
			throw new IllegalStateException(
				"Unable to initialize GCS store", portalException);
		}
	}

	private StorageBatchResult<Boolean> _deleteBlob(
		Blob blob, StorageBatch storageBatch) {

		if (_decryptStorageBlobSourceOption == null) {
			return storageBatch.delete(blob.getBlobId());
		}

		return storageBatch.delete(
			blob.getBlobId(), _decryptStorageBlobSourceOption);
	}

	private void _deleteObjects(String path) {
		try {
			Page<Blob> blobPage = _gcsStore.list(
				_gcsStoreConfiguration.bucketName(),
				Storage.BlobListOption.pageSize(_PAGE_SIZE),
				Storage.BlobListOption.prefix(path));

			Iterable<Blob> blobs = blobPage.iterateAll();

			List<StorageBatchResult<Boolean>> results = new ArrayList<>();

			StorageBatch storageBatch = _gcsStore.batch();

			try {
				blobs.forEach(
					blob -> results.add(_deleteBlob(blob, storageBatch)));
			}
			finally {
				if (!results.isEmpty()) {
					storageBatch.submit();

					for (StorageBatchResult<Boolean> result : results) {
						if ((result == null) || !result.get()) {
							_log.error(
								StringBundler.concat(
									"Unable to delete objects in bucket ",
									_gcsStoreConfiguration.bucketName(), " at ",
									path));

							break;
						}
					}
				}
			}
		}
		catch (StorageException storageException) {
			_log.error("Unable to delete " + path, storageException);
		}
	}

	private BucketInfo _getBucketInfo() {
		if (_bucketInfo == null) {
			_bucketInfo = BucketInfo.newBuilder(
				_gcsStoreConfiguration.bucketName()
			).build();
		}

		return _bucketInfo;
	}

	private String _getDirectoryKey(
		long companyId, long repositoryId, String folderName) {

		return _getFileKey(companyId, repositoryId, folderName);
	}

	private String _getFileKey(
		long companyId, long repositoryId, String fileName) {

		return StoreArea.getCurrentStoreAreaPath(
			companyId, repositoryId, fileName);
	}

	private String[] _getFilePaths(long companyId) {
		List<String> filePaths = new ArrayList<>();

		Bucket bucket = _gcsStore.get(_gcsStoreConfiguration.bucketName());

		String path = StoreArea.getCurrentStoreAreaPath(companyId);

		Page<Blob> blobPage = bucket.list(Storage.BlobListOption.prefix(path));

		Iterable<Blob> blobs = blobPage.iterateAll();

		blobs.forEach(blob -> filePaths.add(blob.getName()));

		return filePaths.toArray(new String[0]);
	}

	private String[] _getFilePaths(
		long companyId, long repositoryId, String dirName) {

		List<String> filePaths = new ArrayList<>();

		Bucket bucket = _gcsStore.get(_gcsStoreConfiguration.bucketName());

		String path = null;

		if (Validator.isNull(dirName) ||
			dirName.equals(StringPool.FORWARD_SLASH)) {

			path = _getRepositoryKey(companyId, repositoryId);
		}
		else {
			path = _getDirectoryKey(companyId, repositoryId, dirName);
		}

		Page<Blob> blobPage = bucket.list(Storage.BlobListOption.prefix(path));

		Iterable<Blob> blobs = blobPage.iterateAll();

		blobs.forEach(blob -> filePaths.add(blob.getName()));

		return filePaths.toArray(new String[0]);
	}

	private String _getFileVersionKey(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		return StoreArea.getCurrentStoreAreaPath(
			companyId, repositoryId, fileName, versionLabel);
	}

	private String _getHeadVersionLabel(
		long companyId, long repositoryId, String fileName,
		String versionLabel) {

		if (Validator.isNotNull(versionLabel)) {
			return _getFileVersionKey(
				companyId, repositoryId, fileName, versionLabel);
		}

		String path = _getFileKey(companyId, repositoryId, fileName);

		String[] fileNames = _getFilePaths(companyId, repositoryId, path);

		if (ArrayUtil.isEmpty(fileNames)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Using default version for " + path);
			}

			return _getFileVersionKey(
				companyId, repositoryId, fileName, VERSION_DEFAULT);
		}

		List<String> fileNamesList = Arrays.asList(fileNames);

		fileNamesList.sort(new VersionNumberComparator());

		return fileNamesList.get(fileNamesList.size() - 1);
	}

	private ReadChannel _getReadChannel(Blob blob) {
		if (_decryptBlobBlobSourceOption == null) {
			return blob.reader();
		}

		return blob.reader(_decryptBlobBlobSourceOption);
	}

	private String _getRepositoryKey(long companyId, long repositoryId) {
		return StoreArea.getCurrentStoreAreaPath(companyId, repositoryId);
	}

	private WriteChannel _getWriteChannel(BlobInfo blobInfo) {
		if (_encryptStorageBlobWriteOption == null) {
			return _gcsStore.writer(blobInfo);
		}

		return _gcsStore.writer(blobInfo, _encryptStorageBlobWriteOption);
	}

	private void _initEncryption() {
		String aes256Key = _secretResolver.resolve(
			CompanyConstants.SYSTEM, _gcsStoreConfiguration.aes256Key());

		if (Validator.isNull(aes256Key)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Files are not encrypted because the portal property " +
						"\"dl.store.gcs.aes256.key\" is not set");
			}

			_decryptBlobBlobSourceOption = null;
			_decryptStorageBlobSourceOption = null;
			_encryptStorageBlobWriteOption = null;
		}
		else {
			_decryptBlobBlobSourceOption = Blob.BlobSourceOption.decryptionKey(
				aes256Key);
			_decryptStorageBlobSourceOption =
				Storage.BlobSourceOption.decryptionKey(aes256Key);
			_encryptStorageBlobWriteOption =
				Storage.BlobWriteOption.encryptionKey(aes256Key);
		}
	}

	private void _initGCSStore() throws PortalException {
		String serviceAccountKey = _secretResolver.resolve(
			CompanyConstants.SYSTEM,
			_gcsStoreConfiguration.serviceAccountKey());

		try {
			if (Validator.isBlank(serviceAccountKey)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Using application default credentials because " +
							"service account key was not set");
				}

				_googleCredentials =
					ServiceAccountCredentials.getApplicationDefault();
			}
			else {
				_googleCredentials = ServiceAccountCredentials.fromStream(
					new ByteArrayInputStream(serviceAccountKey.getBytes()));
			}
		}
		catch (IOException ioException) {
			throw new PortalException(
				"Unable to authenticate with GCS", ioException);
		}

		StorageOptions storageOptions = StorageOptions.newBuilder(
		).setCredentials(
			_googleCredentials
		).setRetrySettings(
			RetrySettings.newBuilder(
			).setInitialRetryDelay(
				Duration.ofMillis(_gcsStoreConfiguration.initialRetryDelay())
			).setInitialRpcTimeout(
				Duration.ofMillis(_gcsStoreConfiguration.initialRPCTimeout())
			).setJittered(
				_gcsStoreConfiguration.retryJitter()
			).setMaxAttempts(
				_gcsStoreConfiguration.maxRetryAttempts()
			).setMaxRetryDelay(
				Duration.ofMillis(_gcsStoreConfiguration.maxRetryDelay())
			).setMaxRpcTimeout(
				Duration.ofMillis(_gcsStoreConfiguration.maxRPCTimeout())
			).setRetryDelayMultiplier(
				_gcsStoreConfiguration.retryDelayMultiplier()
			).setRpcTimeoutMultiplier(
				_gcsStoreConfiguration.rpcTimeoutMultiplier()
			).build()
		).build();

		_gcsStore = storageOptions.getService();
	}

	private String _processStoreArea(
		long companyId, int evictionQuota, Predicate<Blob> predicate,
		String startOffset, StoreArea storeArea,
		TemporalAmount temporalAmount) {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPS-174816")) {
			return StringPool.BLANK;
		}

		Bucket bucket = _gcsStore.get(_gcsStoreConfiguration.bucketName());
		int evictedBlobQuota = Math.max(evictionQuota, 1);
		int evictedBlobsCount = 0;
		Instant instant = Instant.now();
		String lastVisitedBlobName = startOffset;
		StorageBatch storageBatch = _gcsStore.batch();

		int pageSize = Math.max(evictedBlobQuota * 2, 10);
		int visitedPageLimit = Math.max(evictedBlobQuota / 10, 10);

		while ((evictedBlobQuota > 0) && (visitedPageLimit > 0)) {
			boolean emptyPage = true;

			Page<Blob> blobPage = bucket.list(
				Storage.BlobListOption.fields(
					Storage.BlobField.ID, Storage.BlobField.NAME,
					Storage.BlobField.UPDATED),
				Storage.BlobListOption.pageSize(pageSize),
				Storage.BlobListOption.prefix(storeArea.getPath(companyId)),
				Storage.BlobListOption.startOffset(lastVisitedBlobName));

			for (Blob blob : blobPage.getValues()) {
				Instant updateTimeInstant = Instant.ofEpochMilli(
					blob.getUpdateTime());

				Instant evictionInstant = updateTimeInstant.plus(
					temporalAmount);

				if (evictionInstant.isBefore(instant) && predicate.test(blob)) {
					storageBatch.delete(blob.getBlobId());

					evictedBlobQuota--;
					evictedBlobsCount++;
				}

				emptyPage = false;

				lastVisitedBlobName = blob.getName();
			}

			if (evictedBlobsCount >= _EVICTED_BATCH_SIZE) {
				storageBatch.submit();

				evictedBlobsCount = 0;

				storageBatch = _gcsStore.batch();
			}

			if (emptyPage) {
				lastVisitedBlobName = StringPool.BLANK;

				break;
			}

			visitedPageLimit--;
		}

		if (evictedBlobsCount > 0) {
			storageBatch.submit();
		}

		return lastVisitedBlobName;
	}

	private static final int _EVICTED_BATCH_SIZE = 10;

	private static final long _PAGE_SIZE = 50L;

	private static final Log _log = LogFactoryUtil.getLog(GCSStore.class);

	private BucketInfo _bucketInfo;
	private Blob.BlobSourceOption _decryptBlobBlobSourceOption;
	private Storage.BlobSourceOption _decryptStorageBlobSourceOption;
	private Storage.BlobWriteOption _encryptStorageBlobWriteOption;
	private Storage _gcsStore;
	private volatile GCSStoreConfiguration _gcsStoreConfiguration;
	private GoogleCredentials _googleCredentials;

	@Reference
	private SecretResolver _secretResolver;

	private ServiceRegistration<StoreAreaProcessor> _serviceRegistration;

	private class GCSStoreAreaProcessor implements StoreAreaProcessor {

		@Override
		public String cleanUpDeletedStoreArea(
			long companyId, int deletionQuota, Predicate<String> predicate,
			String startOffset, TemporalAmount temporalAmount) {

			return _processStoreArea(
				companyId, deletionQuota,
				blob -> predicate.test(blob.getName()), startOffset,
				StoreArea.DELETED, temporalAmount);
		}

		@Override
		public String cleanUpNewStoreArea(
			long companyId, int evictionQuota, Predicate<String> predicate,
			String startOffset, TemporalAmount temporalAmount) {

			return _processStoreArea(
				companyId, evictionQuota,
				blob -> {
					if (predicate.test(blob.getName())) {
						return copy(
							blob.getName(),
							StoreArea.NEW.relocate(
								blob.getName(), StoreArea.DELETED));
					}

					return copy(
						blob.getName(),
						StoreArea.NEW.relocate(blob.getName(), StoreArea.LIVE));
				},
				startOffset, StoreArea.NEW, temporalAmount);
		}

		@Override
		public boolean copy(String sourceFileName, String destinationFileName) {
			try {
				if (!FeatureFlagManagerUtil.isEnabled(
						CompanyThreadLocal.getCompanyId(), "LPS-174816")) {

					return true;
				}

				CopyWriter copyWriter = _gcsStore.copy(
					Storage.CopyRequest.newBuilder(
					).setSource(
						_gcsStoreConfiguration.bucketName(), sourceFileName
					).setTarget(
						BlobId.of(
							_gcsStoreConfiguration.bucketName(),
							destinationFileName)
					).build());

				while (!copyWriter.isDone()) {
					copyWriter.copyChunk();
				}

				return true;
			}
			catch (StorageException storageException) {
				if (_log.isInfoEnabled()) {
					_log.info(storageException);
				}

				return false;
			}
		}

		@Override
		public boolean copyDirectory(
			long companyId, long repositoryId, String dirName,
			StoreArea[] sourceStoreAreas, StoreArea destinationStoreArea) {

			try {
				if (!FeatureFlagManagerUtil.isEnabled(
						companyId, "LPS-174816")) {

					return true;
				}

				for (StoreArea sourceStoreArea : sourceStoreAreas) {
					String[] filePaths = StoreArea.withStoreArea(
						sourceStoreArea,
						() -> _getFilePaths(companyId, repositoryId, dirName));

					for (String filePath : filePaths) {
						copy(
							filePath,
							sourceStoreArea.relocate(
								filePath, destinationStoreArea));
					}
				}

				return true;
			}
			catch (StorageException storageException) {
				if (_log.isInfoEnabled()) {
					_log.info(storageException);
				}

				return false;
			}
		}

		@Override
		public boolean copyDirectory(
			long companyId, StoreArea[] sourceStoreAreas,
			StoreArea destinationStoreArea) {

			try {
				if (!FeatureFlagManagerUtil.isEnabled(
						companyId, "LPS-174816")) {

					return true;
				}

				for (StoreArea sourceStoreArea : sourceStoreAreas) {
					String[] filePaths = StoreArea.withStoreArea(
						sourceStoreArea, () -> _getFilePaths(companyId));

					for (String filePath : filePaths) {
						copy(
							filePath,
							sourceStoreArea.relocate(
								filePath, destinationStoreArea));
					}
				}

				return true;
			}
			catch (StorageException storageException) {
				if (_log.isInfoEnabled()) {
					_log.info(storageException);
				}

				return false;
			}
		}

	}

}