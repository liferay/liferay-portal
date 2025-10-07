/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.file.install.internal;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.io.BigEndianCodec;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ModuleFrameworkPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Matthew Tambara
 */
public class DirectoryWatcher extends Thread implements BundleListener {

	public DirectoryWatcher(BundleContext bundleContext) throws IOException {
		super("fileinstall-directory-watcher");

		setDaemon(true);

		_bundleContext = bundleContext;

		Bundle bundle = bundleContext.getBundle();

		_checksumRandomAccessFile = new RandomAccessFile(
			bundle.getDataFile("bundles.checksum"), "rw");

		long length = _checksumRandomAccessFile.length();

		if (length > 0) {
			int entryCount = (int)(length / 16);

			byte[] bytes = new byte[entryCount * 16];

			_checksumRandomAccessFile.readFully(bytes);

			int index = 0;

			for (int i = 0; i < entryCount; i++) {
				_bundleChecksums.put(
					BigEndianCodec.getLong(bytes, index),
					BigEndianCodec.getLong(bytes, index + 8));

				index += 16;
			}

			List<Long> currentBundleIds = new ArrayList<>();

			for (Bundle currentBundle : bundleContext.getBundles()) {
				currentBundleIds.add(currentBundle.getBundleId());
			}

			Set<Long> checksumBundleIds = _bundleChecksums.keySet();

			checksumBundleIds.retainAll(currentBundleIds);

			int actualEntryCount = _bundleChecksums.size();

			if (actualEntryCount < entryCount) {
				index = 0;

				for (Map.Entry<Long, Long> entry :
						_bundleChecksums.entrySet()) {

					BigEndianCodec.putLong(bytes, index, entry.getKey());
					BigEndianCodec.putLong(bytes, index + 8, entry.getValue());

					index += 16;
				}

				_checksumRandomAccessFile.seek(0);

				int fileSize = actualEntryCount * 16;

				_checksumRandomAccessFile.write(bytes, 0, fileSize);
				_checksumRandomAccessFile.setLength(fileSize);
			}
		}

		_systemBundle = bundleContext.getBundle(
			Constants.SYSTEM_BUNDLE_LOCATION);

		for (String dir : PropsValues.MODULE_FRAMEWORK_AUTO_DEPLOY_DIRS) {
			String filePath = Util.getFilePath(dir);

			_watchedDirPaths.add(filePath);
			_watchedDirs.add(new File(filePath));
		}

		_fileInstallers = ServiceTrackerListFactory.open(
			_bundleContext, FileInstaller.class, null,
			new ServiceTrackerCustomizer<FileInstaller, FileInstaller>() {

				@Override
				public FileInstaller addingService(
					ServiceReference<FileInstaller> serviceReference) {

					return _bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<FileInstaller> serviceReference,
					FileInstaller fileInstaller) {
				}

				@Override
				public void removedService(
					ServiceReference<FileInstaller> serviceReference,
					FileInstaller fileInstaller) {

					_bundleContext.ungetService(serviceReference);

					_bundleContext.ungetService(serviceReference);

					for (Artifact artifact : _getArtifacts()) {
						if (artifact.getFileInstaller() == fileInstaller) {
							artifact.setFileInstaller(null);
						}
					}

					synchronized (this) {
						notifyAll();
					}
				}

			});

		if (!Validator.isBlank(
				PropsValues.MODULE_FRAMEWORK_FILE_INSTALL_FILTER)) {

			_filenameFilter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					Matcher matcher = _pattern.matcher(name);

					return matcher.matches();
				}

				private final Pattern _pattern = Pattern.compile(
					PropsValues.MODULE_FRAMEWORK_FILE_INSTALL_FILTER);

			};
		}
		else {
			_filenameFilter = (dir, name) -> true;
		}

		_scanner = new Scanner(
			_watchedDirs, _filenameFilter,
			PropsValues.MODULE_FRAMEWORK_FILE_INSTALL_SUBDIR_MODE);

		_bundleContext.addBundleListener(this);
	}

	@Override
	public void bundleChanged(BundleEvent bundleEvent) {
		int type = bundleEvent.getType();

		if (type == BundleEvent.UNINSTALLED) {
			List<Artifact> artifacts = _getArtifacts();

			Iterator<?> iterator = artifacts.iterator();

			while (iterator.hasNext()) {
				Artifact artifact = (Artifact)iterator.next();

				Bundle bundle = bundleEvent.getBundle();

				long bundleId = bundle.getBundleId();

				if (artifact.getBundleId() == bundleId) {
					iterator.remove();

					break;
				}
			}
		}

		if ((type == BundleEvent.INSTALLED) || (type == BundleEvent.RESOLVED) ||
			(type == BundleEvent.UNINSTALLED) ||
			(type == BundleEvent.UNRESOLVED) || (type == BundleEvent.UPDATED)) {

			_setStateChanged(true);
		}
	}

	public void close() throws IOException {
		_bundleContext.removeBundleListener(this);

		interrupt();

		try {
			join(10000);
		}
		catch (InterruptedException interruptedException) {
			if (_log.isDebugEnabled()) {
				_log.debug(interruptedException);
			}
		}

		_fileInstallers.close();

		_checksumRandomAccessFile.close();
	}

	public Scanner getScanner() {
		return _scanner;
	}

	@Override
	public void run() {
		if (!PropsValues.MODULE_FRAMEWORK_FILE_INSTALL_NO_INITIAL_DELAY) {
			try {
				Thread.sleep(PropsValues.MODULE_FRAMEWORK_AUTO_DEPLOY_INTERVAL);
			}
			catch (InterruptedException interruptedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(interruptedException);
				}

				return;
			}

			_initializeCurrentManagedBundles();
		}

		while (!interrupted()) {
			try {
				FrameworkStartLevel frameworkStartLevel = _systemBundle.adapt(
					FrameworkStartLevel.class);

				if ((frameworkStartLevel.getStartLevel() >=
						PropsValues.
							MODULE_FRAMEWORK_FILE_INSTALL_ACTIVE_LEVEL) &&
					(_systemBundle.getState() == Bundle.ACTIVE)) {

					Set<File> files = _scanner.scan(false);

					if (files != null) {
						_process(files);
					}
				}

				synchronized (this) {
					wait(PropsValues.MODULE_FRAMEWORK_AUTO_DEPLOY_INTERVAL);
				}
			}
			catch (InterruptedException interruptedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(interruptedException);
				}

				interrupt();

				return;
			}
			catch (Throwable throwable) {
				try {
					_bundleContext.getBundle();
				}
				catch (IllegalStateException illegalStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(illegalStateException);
					}

					return;
				}

				_log.error(throwable, throwable);
			}
		}
	}

	@Override
	public void start() {
		if (PropsValues.MODULE_FRAMEWORK_FILE_INSTALL_NO_INITIAL_DELAY) {
			_initializeCurrentManagedBundles();

			Set<File> files = _scanner.scan(true);

			if (files != null) {
				try {
					_process(files);
				}
				catch (InterruptedException interruptedException) {
					throw new RuntimeException(interruptedException);
				}
			}
		}

		super.start();
	}

	private boolean _contains(String path, List<String> dirPaths) {
		for (String dirPath : dirPaths) {
			if (path.contains(dirPath)) {
				return true;
			}
		}

		return false;
	}

	private void _findBundlesWithFragmentsToRefresh(Set<Bundle> bundles) {
		Set<String> hostBundleSymbolicNames = new HashSet<>();

		for (Bundle bundle : bundles) {
			if ((bundle.getState() != Bundle.UNINSTALLED) &&
				_isFragment(bundle)) {

				hostBundleSymbolicNames.add(_getFragmentHost(bundle));
			}
		}

		if (hostBundleSymbolicNames.isEmpty()) {
			return;
		}

		for (Bundle bundle : _bundleContext.getBundles()) {
			if (hostBundleSymbolicNames.remove(bundle.getSymbolicName())) {
				int hostBundleState = bundle.getState();

				if ((hostBundleState == Bundle.ACTIVE) ||
					(hostBundleState == Bundle.RESOLVED) ||
					(hostBundleState == Bundle.STARTING)) {

					bundles.add(bundle);
				}
			}
		}
	}

	private FileInstaller _findFileInstaller(
		File file, Iterable<FileInstaller> iterable) {

		for (FileInstaller fileInstaller : iterable) {
			if (fileInstaller.canTransformURL(file)) {
				return fileInstaller;
			}
		}

		return null;
	}

	private Artifact _getArtifact(File file) {
		synchronized (_currentManagedArtifacts) {
			return _currentManagedArtifacts.get(file);
		}
	}

	private List<Artifact> _getArtifacts() {
		synchronized (_currentManagedArtifacts) {
			return new ArrayList<>(_currentManagedArtifacts.values());
		}
	}

	private long _getChecksum(Bundle bundle) {
		Long checksum = _bundleChecksums.get(bundle.getBundleId());

		if (checksum == null) {
			return Long.MIN_VALUE;
		}

		return checksum;
	}

	/**
	 * @see com.liferay.portal.fragment.bundle.watcher.internal.PortalFragmentBundleWatcher#_getFragmentHost
	 */
	private String _getFragmentHost(Bundle bundle) {
		Dictionary<String, String> dictionary = bundle.getHeaders(
			StringPool.BLANK);

		String fragmentHost = dictionary.get(Constants.FRAGMENT_HOST);

		if (fragmentHost == null) {
			return null;
		}

		int index = fragmentHost.indexOf(CharPool.SEMICOLON);

		if (index != -1) {
			fragmentHost = fragmentHost.substring(0, index);
		}

		return fragmentHost;
	}

	private void _initializeCurrentManagedBundles() {
		Bundle[] bundles = _bundleContext.getBundles();

		Map<File, Long> checksums = new HashMap<>();

		for (Bundle bundle : bundles) {
			String location = bundle.getLocation();

			URI uri = null;

			try {
				uri = new URI(location);
				uri = uri.normalize();
			}
			catch (URISyntaxException uriSyntaxException) {
				if (_log.isDebugEnabled()) {
					_log.debug(uriSyntaxException);
				}

				File file = new File(location);

				uri = file.toURI();

				uri = uri.normalize();
			}

			String locationPath = uri.getPath();

			if (locationPath == null) {
				continue;
			}

			String path = null;

			if ((location != null) &&
				_contains(locationPath, _watchedDirPaths)) {

				String schemeSpecificPart = uri.getSchemeSpecificPart();

				if (uri.isOpaque() && (schemeSpecificPart != null)) {
					int lastIndexOfFileProtocol =
						schemeSpecificPart.lastIndexOf("file:");

					int offsetFileProtocol = 0;

					if (lastIndexOfFileProtocol >= 0) {
						offsetFileProtocol =
							lastIndexOfFileProtocol + "file:".length();
					}

					int firstIndexOfDollar = schemeSpecificPart.indexOf(
						StringPool.DOLLAR);

					int endOfPath = schemeSpecificPart.length();

					if (firstIndexOfDollar >= 0) {
						endOfPath = firstIndexOfDollar;
					}

					path = schemeSpecificPart.substring(
						offsetFileProtocol, endOfPath);
				}
				else {
					path = uri.getPath();
				}
			}

			if (path == null) {
				continue;
			}

			int index = path.lastIndexOf(CharPool.SLASH);

			if ((index != -1) && _startWith(path, _watchedDirPaths)) {
				if (!_filenameFilter.accept(
						new File(path.substring(0, index)),
						path.substring(index + 1))) {

					continue;
				}

				Artifact artifact = new Artifact();

				artifact.setBundleId(bundle.getBundleId());
				artifact.setChecksum(_getChecksum(bundle));
				artifact.setFile(new File(path));

				_setArtifact(new File(path), artifact);

				checksums.put(new File(path), artifact.getChecksum());
			}
		}

		_scanner.initialize(checksums);
	}

	private Bundle _install(Artifact artifact) {
		File file = artifact.getFile();

		Bundle bundle = null;

		AtomicBoolean modified = new AtomicBoolean();

		try {
			FileInstaller fileInstaller = _findFileInstaller(
				file, _fileInstallers);

			if (fileInstaller == null) {
				_processingFailures.add(file);

				return null;
			}

			artifact.setFileInstaller(fileInstaller);

			long checksum = artifact.getChecksum();

			Artifact badArtifact = _installationFailures.get(file);

			if ((badArtifact != null) &&
				(badArtifact.getChecksum() == checksum)) {

				return null;
			}

			URL url = fileInstaller.transformURL(file);

			if (url != null) {
				String location = url.toString();

				try (InputStream inputStream = url.openStream();
					BufferedInputStream bufferedInputStream =
						new BufferedInputStream(inputStream)) {

					bundle = _installOrUpdateBundle(
						location, bufferedInputStream, checksum, modified);

					artifact.setBundleId(bundle.getBundleId());
				}
			}

			_installationFailures.remove(file);
			_setArtifact(file, artifact);
		}
		catch (Exception exception) {
			_log.error("Unable to install artifact: " + file, exception);

			_installationFailures.put(file, artifact);
		}

		if (modified.get()) {
			return bundle;
		}

		return null;
	}

	private Collection<Bundle> _install(Collection<Artifact> artifacts) {
		List<Bundle> bundles = new ArrayList<>();

		for (Artifact artifact : artifacts) {
			Bundle bundle = _install(artifact);

			if (bundle != null) {
				bundles.add(bundle);
			}
		}

		return bundles;
	}

	private Bundle _installOrUpdateBundle(
			String location, BufferedInputStream bufferedInputStream,
			long checksum, AtomicBoolean modified)
		throws Exception {

		Bundle bundle = _bundleContext.getBundle(location);

		if ((bundle != null) && (_getChecksum(bundle) != checksum)) {
			bundle.update(bufferedInputStream);

			_putChecksum(bundle, checksum);

			return bundle;
		}

		bufferedInputStream.mark(256 * 1024);

		try (JarInputStream jarInputStream = new JarInputStream(
				bufferedInputStream)) {

			Manifest manifest = jarInputStream.getManifest();

			if (manifest == null) {
				throw new BundleException(
					StringBundler.concat(
						"The bundle ", location, " does not have a ",
						"META-INF/MANIFEST.MF! Make sure, META-INF and ",
						"MANIFEST.MF are the first 2 entries in your JAR!"));
			}

			Attributes attributes = manifest.getMainAttributes();

			String symbolicName = attributes.getValue(
				Constants.BUNDLE_SYMBOLICNAME);

			String versionString = attributes.getValue(
				Constants.BUNDLE_VERSION);

			Version version = Version.emptyVersion;

			if (versionString != null) {
				version = Version.parseVersion(versionString);
			}

			for (Bundle currentBundle : _bundleContext.getBundles()) {
				String currentSymbolicName = currentBundle.getSymbolicName();

				if ((currentSymbolicName != null) &&
					Objects.equals(currentSymbolicName, symbolicName)) {

					Dictionary<String, String> headers =
						currentBundle.getHeaders(StringPool.BLANK);

					versionString = headers.get(Constants.BUNDLE_VERSION);

					Version currentVersion = Version.emptyVersion;

					if (versionString != null) {
						currentVersion = Version.parseVersion(versionString);
					}

					if (version.equals(currentVersion)) {
						bufferedInputStream.reset();

						if (_getChecksum(currentBundle) != checksum) {
							if (_log.isWarnEnabled()) {
								_log.warn(
									StringBundler.concat(
										"A bundle with the same symbolic name ",
										"(", symbolicName, ") and version (",
										versionString,
										") is already installed. Updating ",
										"this bundle instead."));
							}

							_stopTransient(currentBundle);

							_putChecksum(currentBundle, checksum);

							currentBundle.update(bufferedInputStream);

							modified.set(true);
						}

						return currentBundle;
					}
				}
			}

			bufferedInputStream.reset();

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Installing bundle ", symbolicName, " / ", version));
			}

			bundle = _bundleContext.installBundle(
				location, bufferedInputStream);

			if (bundle.getState() == Bundle.UNINSTALLED) {
				return bundle;
			}

			_putChecksum(bundle, checksum);

			modified.set(true);

			Dictionary<String, String> headers = bundle.getHeaders("");

			String header = headers.get("Web-ContextPath");

			BundleStartLevel bundleStartLevel = bundle.adapt(
				BundleStartLevel.class);

			if (header != null) {
				bundleStartLevel.setStartLevel(
					ModuleFrameworkPropsValues.
						MODULE_FRAMEWORK_WEB_START_LEVEL);
			}
			else if (ModuleFrameworkPropsValues.
						MODULE_FRAMEWORK_DYNAMIC_INSTALL_START_LEVEL != 0) {

				bundleStartLevel.setStartLevel(
					ModuleFrameworkPropsValues.
						MODULE_FRAMEWORK_DYNAMIC_INSTALL_START_LEVEL);
			}

			return bundle;
		}
	}

	/**
	 * @see com.liferay.portal.fragment.bundle.watcher.internal.PortalFragmentBundleWatcher#_isFragment
	 */
	private boolean _isFragment(Bundle bundle) {
		BundleRevision bundleRevision = bundle.adapt(BundleRevision.class);

		if ((bundleRevision.getTypes() & BundleRevision.TYPE_FRAGMENT) != 0) {
			return true;
		}

		return false;
	}

	private boolean _isStateChanged() {
		return _stateChanged.get();
	}

	private void _process(Set<File> files) throws InterruptedException {
		List<Artifact> createdArtifacts = new ArrayList<>();
		List<Artifact> deletedArtifacts = new ArrayList<>();
		List<Artifact> modifiedArtifacts = new ArrayList<>();

		synchronized (_processingFailures) {
			files.addAll(_processingFailures);

			_processingFailures.clear();
		}

		for (File file : files) {
			Artifact artifact = _getArtifact(file);

			if (!file.exists()) {
				if (artifact != null) {
					deletedArtifacts.add(artifact);
				}
			}
			else {
				if (artifact != null) {
					artifact.setChecksum(_scanner.getChecksum(file));

					modifiedArtifacts.add(artifact);
				}
				else {
					artifact = new Artifact();

					artifact.setChecksum(_scanner.getChecksum(file));
					artifact.setFile(file);

					createdArtifacts.add(artifact);
				}
			}
		}

		Collection<Bundle> uninstalledBundles = _uninstall(deletedArtifacts);

		Collection<Bundle> updatedBundles = _update(modifiedArtifacts);

		Collection<Bundle> installedBundles = _install(createdArtifacts);

		if (!uninstalledBundles.isEmpty() || !updatedBundles.isEmpty() ||
			!installedBundles.isEmpty()) {

			Set<Bundle> bundles = new HashSet<>();

			bundles.addAll(uninstalledBundles);

			bundles.addAll(updatedBundles);

			bundles.addAll(installedBundles);

			_findBundlesWithFragmentsToRefresh(bundles);

			if (!bundles.isEmpty()) {
				_refresh(bundles);

				_setStateChanged(true);
			}
		}

		if (PropsValues.MODULE_FRAMEWORK_FILE_INSTALL_BUNDLES_START_NEW) {
			FrameworkStartLevel frameworkStartLevel = _systemBundle.adapt(
				FrameworkStartLevel.class);

			int startLevel = frameworkStartLevel.getStartLevel();

			if (_isStateChanged() || (startLevel != _frameworkStartLevel)) {
				_frameworkStartLevel = startLevel;

				_startAllBundles();

				_delayedStart.addAll(installedBundles);
				_delayedStart.removeAll(uninstalledBundles);

				_startBundles(_delayedStart);

				_consistentlyFailingBundles.clear();

				_consistentlyFailingBundles.addAll(_delayedStart);

				_setStateChanged(false);
			}
		}
	}

	private void _putChecksum(Bundle bundle, long checksum) {
		long bundleId = bundle.getBundleId();

		_bundleChecksums.put(bundleId, checksum);

		byte[] bytes = new byte[16];

		BigEndianCodec.putLong(bytes, 0, bundleId);
		BigEndianCodec.putLong(bytes, 8, checksum);

		try {
			_checksumRandomAccessFile.write(bytes);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _refresh(Collection<Bundle> bundles)
		throws InterruptedException {

		FrameworkWiring frameworkWiring = _systemBundle.adapt(
			FrameworkWiring.class);

		DefaultNoticeableFuture<FrameworkEvent> defaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

		frameworkWiring.refreshBundles(
			bundles,
			frameworkEvent -> defaultNoticeableFuture.set(frameworkEvent));

		try {
			FrameworkEvent frameworkEvent = defaultNoticeableFuture.get();

			if (frameworkEvent.getType() != FrameworkEvent.PACKAGES_REFRESHED) {
				throw frameworkEvent.getThrowable();
			}
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}
	}

	private void _removeArtifact(File file) {
		synchronized (_currentManagedArtifacts) {
			_currentManagedArtifacts.remove(file);
		}
	}

	private void _setArtifact(File file, Artifact artifact) {
		synchronized (_currentManagedArtifacts) {
			_currentManagedArtifacts.put(file, artifact);
		}
	}

	private void _setStateChanged(boolean changed) {
		_stateChanged.set(changed);
	}

	private void _startAllBundles() {
		FrameworkStartLevel frameworkStartLevel = _systemBundle.adapt(
			FrameworkStartLevel.class);

		Set<Bundle> bundles = new LinkedHashSet<>();

		for (Artifact artifact : _getArtifacts()) {
			long bundleId = artifact.getBundleId();

			if (bundleId > 0) {
				Bundle bundle = _bundleContext.getBundle(bundleId);

				if (bundle != null) {
					int state = bundle.getState();

					BundleStartLevel bundleStartLevel = bundle.adapt(
						BundleStartLevel.class);

					if ((state != Bundle.STARTING) &&
						(state != Bundle.ACTIVE) &&
						(PropsValues.
							MODULE_FRAMEWORK_FILE_INSTALL_BUNDLES_START_TRANSIENT ||
						 bundleStartLevel.isPersistentlyStarted()) &&
						(frameworkStartLevel.getStartLevel() >=
							bundleStartLevel.getStartLevel())) {

						bundles.add(bundle);
					}
				}
			}
		}

		_startBundles(bundles);
	}

	private boolean _startBundle(Bundle bundle, boolean logFailures) {
		BundleStartLevel bundleStartLevel = bundle.adapt(
			BundleStartLevel.class);

		if (PropsValues.MODULE_FRAMEWORK_FILE_INSTALL_BUNDLES_START_NEW &&
			(bundle.getState() != Bundle.UNINSTALLED) && !_isFragment(bundle) &&
			(_frameworkStartLevel >= bundleStartLevel.getStartLevel())) {

			try {
				int options = 0;

				if (PropsValues.
						MODULE_FRAMEWORK_FILE_INSTALL_BUNDLES_START_TRANSIENT) {

					options = Bundle.START_TRANSIENT;
				}

				if (PropsValues.
						MODULE_FRAMEWORK_FILE_INSTALL_BUNDLES_START_ACTIVATION_POLICY) {

					options |= Bundle.START_ACTIVATION_POLICY;
				}

				bundle.start(options);

				if (_log.isInfoEnabled()) {
					_log.info("Started bundle: " + bundle.getLocation());
				}

				return true;
			}
			catch (IllegalStateException illegalStateException) {
				if (bundle.getState() == Bundle.UNINSTALLED) {
					return true;
				}

				throw illegalStateException;
			}
			catch (BundleException bundleException) {
				if (logFailures) {
					_log.error(
						"Unable to start bundle: " + bundle.getLocation(),
						bundleException);
				}
			}
		}

		return false;
	}

	private void _startBundles(Set<Bundle> bundles) {
		Iterator<Bundle> iterator = bundles.iterator();

		while (iterator.hasNext()) {
			Bundle bundle = iterator.next();

			if (_startBundle(bundle, true)) {
				iterator.remove();
			}
		}
	}

	private boolean _startWith(String path, List<String> dirPaths) {
		for (String dirPath : dirPaths) {
			if (path.startsWith(dirPath)) {
				return true;
			}
		}

		return false;
	}

	private void _stopTransient(Bundle bundle) throws BundleException {
		if (PropsValues.MODULE_FRAMEWORK_FILE_INSTALL_BUNDLES_START_NEW &&
			!_isFragment(bundle)) {

			bundle.stop(Bundle.STOP_TRANSIENT);
		}
	}

	private Bundle _uninstall(Artifact artifact) {
		try {
			File file = artifact.getFile();

			_removeArtifact(file);

			FileInstaller fileInstaller = artifact.getFileInstaller();

			if (fileInstaller != null) {
				fileInstaller.uninstall(file);
			}

			long bundleId = artifact.getBundleId();

			if (bundleId > 0) {
				Bundle bundle = _bundleContext.getBundle(bundleId);

				if (bundle == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to uninstall bundle: ", file,
								" with id: ", bundleId,
								". The bundle has already been uninstalled"));
					}

					return null;
				}

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"Uninstalling bundle ", bundle.getBundleId(), " (",
							bundle.getSymbolicName(),
							StringPool.CLOSE_PARENTHESIS));
				}

				bundle.uninstall();

				return bundle;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to uninstall artifact: " + artifact.getFile(),
					exception);
			}
		}

		return null;
	}

	private Collection<Bundle> _uninstall(Collection<Artifact> artifacts) {
		List<Bundle> bundles = new ArrayList<>();

		for (Artifact artifact : artifacts) {
			Bundle bundle = _uninstall(artifact);

			if (bundle != null) {
				bundles.add(bundle);
			}
		}

		return bundles;
	}

	private Bundle _update(Artifact artifact) {
		Bundle bundle = null;

		try {
			File file = artifact.getFile();

			FileInstaller fileInstaller = _findFileInstaller(
				file, _fileInstallers);

			if (fileInstaller == null) {
				_processingFailures.add(file);

				return null;
			}

			artifact.setFileInstaller(fileInstaller);

			URL url = fileInstaller.transformURL(file);

			if (url == null) {
				return null;
			}

			long bundleId = artifact.getBundleId();

			bundle = _bundleContext.getBundle(bundleId);

			if (bundle == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to update bundle: ", file, " with ID ",
							bundleId, ". The bundle has been uninstalled"));
				}

				return null;
			}

			if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"Updating bundle ", bundle.getSymbolicName(), " / ",
						bundle.getVersion()));
			}

			_stopTransient(bundle);

			_putChecksum(bundle, artifact.getChecksum());

			try (InputStream inputStream = url.openStream()) {
				bundle.update(inputStream);
			}
		}
		catch (Throwable throwable) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to update artifact " + artifact.getFile(),
					throwable);
			}
		}

		return bundle;
	}

	private Collection<Bundle> _update(Collection<Artifact> artifacts) {
		List<Bundle> bundles = new ArrayList<>();

		for (Artifact artifact : artifacts) {
			Bundle bundle = _update(artifact);

			if (bundle != null) {
				bundles.add(bundle);
			}
		}

		return bundles;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DirectoryWatcher.class);

	private final Map<Long, Long> _bundleChecksums = new HashMap<>();
	private final BundleContext _bundleContext;
	private final RandomAccessFile _checksumRandomAccessFile;
	private final Set<Bundle> _consistentlyFailingBundles = new HashSet<>();
	private final Map<File, Artifact> _currentManagedArtifacts =
		new HashMap<>();
	private final Set<Bundle> _delayedStart = new HashSet<>();
	private final ServiceTrackerList<FileInstaller> _fileInstallers;
	private final FilenameFilter _filenameFilter;
	private int _frameworkStartLevel;
	private final Map<File, Artifact> _installationFailures = new HashMap<>();
	private final Set<File> _processingFailures = new HashSet<>();
	private final Scanner _scanner;
	private final AtomicBoolean _stateChanged = new AtomicBoolean();
	private final Bundle _systemBundle;
	private final List<String> _watchedDirPaths = new ArrayList<>();
	private final List<File> _watchedDirs = new ArrayList<>();

}