/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mail.util;

import com.liferay.mail.kernel.model.Filter;
import com.liferay.mail.kernel.util.Hook;
import com.liferay.petra.process.LoggingOutputProcessor;
import com.liferay.petra.process.ProcessUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsUtil;

import java.io.File;
import java.io.FileReader;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Brian Wing Shun Chan
 */
public class SendmailHook implements Hook {

	@Override
	public void addForward(
		long companyId, long userId, List<Filter> filters,
		List<String> emailAddresses, boolean leaveCopy) {

		try {
			if (emailAddresses != null) {
				String home = PropsUtil.get(PropsKeys.MAIL_HOOK_SENDMAIL_HOME);

				File file = new File(
					StringBundler.concat(
						home, "/", String.valueOf(userId), "/.forward"));

				if (!emailAddresses.isEmpty()) {
					StringBundler sb = new StringBundler(
						emailAddresses.size() * 2);

					for (String emailAddress : emailAddresses) {
						sb.append(emailAddress);
						sb.append("\n");
					}

					FileUtil.write(file, sb.toString());
				}
				else {
					file.delete();
				}
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	@Override
	public void addUser(
		long companyId, long userId, String password, String firstName,
		String middleName, String lastName, String emailAddress) {

		// Get add user command

		String addUserCmd = PropsUtil.get(
			PropsKeys.MAIL_HOOK_SENDMAIL_ADD_USER);

		// Replace userId

		addUserCmd = StringUtil.replace(
			addUserCmd, "%1%", String.valueOf(userId));

		try {
			Future<?> future = ProcessUtil.execute(
				new LoggingOutputProcessor(
					(stdErr, line) -> {
						if (stdErr) {
							_log.error(line);
						}
						else if (_log.isInfoEnabled()) {
							_log.info(line);
						}
					}),
				StringUtil.split(addUserCmd, StringPool.SPACE));

			future.get();
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		updatePassword(companyId, userId, password);
		updateEmailAddress(companyId, userId, emailAddress);
	}

	@Override
	public void addVacationMessage(
		long companyId, long userId, String emailAddress,
		String vacationMessage) {
	}

	@Override
	public void deleteEmailAddress(long companyId, long userId) {
		updateEmailAddress(companyId, userId, "");
	}

	@Override
	public void deleteUser(long companyId, long userId) {
		deleteEmailAddress(companyId, userId);

		// Get delete user command

		String deleteUserCmd = PropsUtil.get(
			PropsKeys.MAIL_HOOK_SENDMAIL_DELETE_USER);

		// Replace userId

		deleteUserCmd = StringUtil.replace(
			deleteUserCmd, "%1%", String.valueOf(userId));

		try {
			Future<?> future = ProcessUtil.execute(
				new LoggingOutputProcessor(
					(stdErr, line) -> {
						if (stdErr) {
							_log.error(line);
						}
						else if (_log.isInfoEnabled()) {
							_log.info(line);
						}
					}),
				StringUtil.split(deleteUserCmd, StringPool.SPACE));

			future.get();
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	@Override
	public void updateBlocked(
		long companyId, long userId, List<String> blocked) {

		String home = PropsUtil.get(PropsKeys.MAIL_HOOK_SENDMAIL_HOME);

		File file = new File(
			StringBundler.concat(
				home, "/", String.valueOf(userId), "/.procmailrc"));

		if (ListUtil.isEmpty(blocked)) {
			file.delete();

			return;
		}

		StringBundler sb = new StringBundler(blocked.size() * 9 + 3);

		sb.append("ORGMAIL /var/spool/mail/$LOGNAME\n");
		sb.append("MAILDIR $HOME/\n");
		sb.append("SENDMAIL /usr/smin/sendmail\n");

		for (String emailAddress : blocked) {
			sb.append("\n");
			sb.append(":0\n");
			sb.append("* ^From.*");
			sb.append(emailAddress);
			sb.append("\n");
			sb.append("{\n");
			sb.append(":0\n");
			sb.append("/dev/null\n");
			sb.append("}\n");
		}

		try {
			FileUtil.write(file, sb.toString());
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	@Override
	public void updateEmailAddress(
		long companyId, long userId, String emailAddress) {

		try {
			String virtusertable = PropsUtil.get(
				PropsKeys.MAIL_HOOK_SENDMAIL_VIRTUSERTABLE);

			StringBundler sb = new StringBundler();

			try (FileReader fileReader = new FileReader(virtusertable);
				UnsyncBufferedReader unsyncBufferedReader =
					new UnsyncBufferedReader(fileReader)) {

				for (String s = unsyncBufferedReader.readLine(); s != null;
					s = unsyncBufferedReader.readLine()) {

					if (!s.endsWith(" " + userId)) {
						sb.append(s);
						sb.append('\n');
					}
				}

				if ((emailAddress != null) && !emailAddress.equals("")) {
					sb.append(emailAddress);
					sb.append(" ");
					sb.append(userId);
					sb.append('\n');
				}
			}

			FileUtil.write(virtusertable, sb.toString());

			String virtusertableRefreshCmd = PropsUtil.get(
				PropsKeys.MAIL_HOOK_SENDMAIL_VIRTUSERTABLE_REFRESH);

			Future<?> future = ProcessUtil.execute(
				new LoggingOutputProcessor(
					(stdErr, line) -> {
						if (stdErr) {
							_log.error(line);
						}
						else if (_log.isInfoEnabled()) {
							_log.info(line);
						}
					}),
				StringUtil.split(virtusertableRefreshCmd, StringPool.SPACE));

			future.get();
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	@Override
	public void updatePassword(long companyId, long userId, String password) {

		// Get change password command

		String changePasswordCmd = PropsUtil.get(
			PropsKeys.MAIL_HOOK_SENDMAIL_CHANGE_PASSWORD);

		// Replace userId and password

		String[] arguments = StringUtil.split(
			changePasswordCmd, StringPool.SPACE);

		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i].equals("%1%")) {
				arguments[i] = String.valueOf(userId);
			}
			else if (arguments[i].equals("%2%")) {
				arguments[i] = password;
			}
		}

		try {
			Future<?> future = ProcessUtil.execute(
				new LoggingOutputProcessor(
					(stdErr, line) -> {
						if (stdErr) {
							_log.error(line);
						}
						else if (_log.isInfoEnabled()) {
							_log.info(line);
						}
					}),
				arguments);

			future.get();
		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(SendmailHook.class);

}