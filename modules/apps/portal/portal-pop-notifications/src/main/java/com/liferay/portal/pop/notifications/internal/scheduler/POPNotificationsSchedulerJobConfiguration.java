/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.pop.notifications.internal.scheduler;

import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.pop.MessageListener;
import com.liferay.portal.kernel.pop.MessageListenerException;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.TriggerConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.pop.notifications.internal.MessageListenerWrapper;
import com.liferay.portal.util.PropsValues;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.InternetAddress;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = SchedulerJobConfiguration.class)
public class POPNotificationsSchedulerJobConfiguration
	implements SchedulerJobConfiguration {

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _companyLocalService.forEachCompanyId(
			companyId -> {
				if (!PrefsPropsUtil.getBoolean(
						companyId, PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED,
						PropsValues.POP_SERVER_NOTIFICATIONS_ENABLED)) {

					return;
				}

				_popNotifications(companyId);
			});
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
			1, TimeUnit.MINUTE);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_messageListenerWrappers = ServiceTrackerListFactory.open(
			bundleContext, MessageListener.class, null,
			new ServiceTrackerCustomizer
				<MessageListener, MessageListenerWrapper>() {

				@Override
				public MessageListenerWrapper addingService(
					ServiceReference<MessageListener> serviceReference) {

					return new MessageListenerWrapper(
						bundleContext.getService(serviceReference));
				}

				@Override
				public void modifiedService(
					ServiceReference<MessageListener> serviceReference,
					MessageListenerWrapper messageListenerWrapper) {
				}

				@Override
				public void removedService(
					ServiceReference<MessageListener> serviceReference,
					MessageListenerWrapper sessageListenerWrapper) {

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_messageListenerWrappers.close();
	}

	private String _getEmailAddress(Address[] addresses) {
		if (ArrayUtil.isEmpty(addresses)) {
			return StringPool.BLANK;
		}

		InternetAddress internetAddress = (InternetAddress)addresses[0];

		return internetAddress.getAddress();
	}

	private List<String> _getEmailAddresses(Address[] addresses) {
		if (ArrayUtil.isEmpty(addresses)) {
			return new ArrayList<>();
		}

		List<String> emailAddresses = new ArrayList<>();

		for (Address address : addresses) {
			InternetAddress internetAddress = (InternetAddress)address;

			emailAddresses.add(internetAddress.getAddress());
		}

		return emailAddresses;
	}

	private Folder _getInboxFolder(Store store) throws MessagingException {
		Folder defaultFolder = store.getDefaultFolder();

		Folder[] folders = defaultFolder.list();

		if (folders.length == 0) {
			throw new MessagingException("Inbox not found");
		}

		Folder inboxFolder = folders[0];

		inboxFolder.open(Folder.READ_WRITE);

		return inboxFolder;
	}

	private Store _getStore(long companyId) throws MessagingException {
		Session session = _mailService.getSession(companyId);

		String storeProtocol = GetterUtil.getString(
			session.getProperty("mail.store.protocol"));

		if (!storeProtocol.equals(Account.PROTOCOL_POPS)) {
			storeProtocol = Account.PROTOCOL_POP;
		}

		Store store = session.getStore(storeProtocol);

		String prefix = "mail." + storeProtocol + ".";

		String host = session.getProperty(prefix + "host");

		String user = session.getProperty(prefix + "user");

		if (Validator.isNull(user)) {
			user = session.getProperty("mail.smtp.user");
		}

		String password = session.getProperty(prefix + "password");

		if (Validator.isNull(password)) {
			password = session.getProperty("mail.smtp.password");
		}

		store.connect(host, user, password);

		return store;
	}

	private void _notifyMessageListeners(Message[] messages)
		throws MessagingException {

		if (_log.isDebugEnabled()) {
			_log.debug("Messages " + messages.length);
		}

		for (Message message : messages) {
			if (_log.isDebugEnabled()) {
				_log.debug("Message " + message);
			}

			String from = _getEmailAddress(message.getFrom());

			List<String> recipients = _getEmailAddresses(
				message.getRecipients(Message.RecipientType.TO));

			if (_log.isDebugEnabled()) {
				_log.debug("From " + from);
				_log.debug("Recipients " + recipients.toString());
			}

			for (MessageListener messageListener : _messageListenerWrappers) {
				try {
					if (messageListener.accept(from, recipients, message)) {
						messageListener.deliver(from, recipients, message);
					}
				}
				catch (MessageListenerException messageListenerException) {
					_log.error(messageListenerException);
				}
			}
		}
	}

	private void _popNotifications(long companyId) throws MessagingException {
		Store store = null;

		try {
			store = _getStore(companyId);

			Folder inboxFolder = _getInboxFolder(store);

			if (inboxFolder == null) {
				return;
			}

			try {
				Message[] messages = inboxFolder.getMessages();

				if (messages == null) {
					return;
				}

				if (_log.isDebugEnabled()) {
					_log.debug("Deleting messages");
				}

				inboxFolder.setFlags(
					messages, new Flags(Flags.Flag.DELETED), true);

				_notifyMessageListeners(messages);
			}
			finally {
				inboxFolder.close(true);
			}
		}
		finally {
			if (store != null) {
				store.close();
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		POPNotificationsSchedulerJobConfiguration.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private MailService _mailService;

	private ServiceTrackerList<MessageListenerWrapper> _messageListenerWrappers;

}