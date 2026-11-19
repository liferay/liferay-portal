/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TextHighlight} from '@clayui/core';
import ClayDropDown from '@clayui/drop-down';
import {useNavigation} from '@clayui/shared';
import Sticker from '@clayui/sticker';
import {ReactPortal} from '@liferay/frontend-js-react-web';
import {ScreenReaderAnnouncer} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {TEditor} from 'frontend-editor-ckeditor-web';
import {sub} from 'frontend-js-web';
import React, {
	useCallback,
	useEffect,
	useId,
	useReducer,
	useRef,
	useState,
} from 'react';

import {getInitials, getStickerColor} from '../../../common/utils/stickerUtil';
import CommentService, {
	MentionUser,
	MentionUserGroup,
} from '../../services/CommentService';
import {
	Mention,
	MentionQuery,
	MentionUserQuery,
} from '../../utils/getCommentMentionConfig';

type Status = 'loaded' | 'loading' | 'searching';

export default function MentionUserPicker({
	editor,
	getMentionUsersURL,
}: {
	editor: TEditor;
	getMentionUsersURL: string;
}) {
	const [activeIndex, setActiveIndex] = useState(0);
	const [keyboardNavigation, setKeyboardNavigation] = useState(false);
	const [query, setQuery] = useState<MentionQuery | null>(null);
	const [results, setResults] = useState<{
		keywords?: string;
		userGroups: MentionUserGroup[];
		users: MentionUser[];
	}>({userGroups: [], users: []});
	const [status, setStatus] = useState<Status>('loading');
	const [, updateAnchor] = useReducer((count: number) => count + 1, 0);

	const anchorRef = useRef<HTMLElement>(null);
	const editableRef = useRef(editor.ui.getEditableElement() ?? null);
	const menuRef = useRef<HTMLDivElement>(null);

	const id = useId();

	const keywords = query?.keywords;
	const {userGroups, users} = results;

	const mentions: Mention[] = [
		...users.map(
			(user): Mention => ({
				id: user.id,
				name: user.name || user.screenName,
				type: 'user',
			})
		),
		...userGroups.map(
			(userGroup): Mention => ({
				id: userGroup.id,
				name: userGroup.name,
				type: 'usergroup',
			})
		),
	];

	const open = !!query && (!!mentions.length || status !== 'loading');

	const activeDescendant =
		open && mentions.length ? `${id}-${activeIndex}` : null;
	const listboxId = `${id}-listbox`;

	const close = useCallback(() => {
		setKeyboardNavigation(false);
		setQuery(null);
		setResults({userGroups: [], users: []});
		setStatus('loading');
	}, []);

	useEffect(() => {
		if (keywords === undefined) {
			return;
		}

		let cancelled = false;

		const searchingTimeout = setTimeout(() => setStatus('searching'), 300);

		CommentService.getMentionUsers({
			keywords,
			url: getMentionUsersURL,
		}).then(({data, error}) => {
			if (cancelled) {
				return;
			}

			clearTimeout(searchingTimeout);

			if (error) {
				close();

				return;
			}

			setActiveIndex(0);
			setResults({
				keywords,
				userGroups: data?.userGroups ?? [],
				users: data?.users ?? [],
			});
			setStatus('loaded');
		});

		return () => {
			cancelled = true;

			clearTimeout(searchingTimeout);
		};
	}, [close, getMentionUsersURL, keywords]);

	const {navigationProps} = useNavigation({
		active: `${id}-${activeIndex}`,
		containerRef: menuRef,
		loop: true,
		onNavigate: (_, index) => {
			setActiveIndex(index!);
			setKeyboardNavigation(true);
		},
		orientation: 'vertical',
		visible: open,
	});

	useEffect(() => {
		const {view} = editor.editing;

		const root = view.document.getRoot();

		if (!root) {
			return;
		}

		view.change((writer) => {
			writer.setAttribute('aria-autocomplete', 'list', root);

			if (activeDescendant) {
				writer.setAttribute(
					'aria-activedescendant',
					activeDescendant,
					root
				);
				writer.setAttribute('aria-controls', listboxId, root);
			}
			else {
				writer.removeAttribute('aria-activedescendant', root);
				writer.removeAttribute('aria-controls', root);
			}
		});
	}, [activeDescendant, editor, listboxId]);

	const select = (mention: Mention) => {
		query?.insert(mention);

		close();
	};

	useEffect(() => {
		const mentionUserQuery = editor.plugins.get(MentionUserQuery);

		mentionUserQuery.handler = {
			onKeyDown: (event) => {
				if (!open || !mentions.length) {
					return false;
				}

				if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
					navigationProps.onKeyDown(
						event as unknown as React.KeyboardEvent<HTMLElement>
					);

					return event.defaultPrevented;
				}

				if (
					event.key === 'Enter' ||
					(event.key === 'Tab' && !event.shiftKey)
				) {
					if (results.keywords === keywords) {
						select(mentions[activeIndex]);
					}

					return true;
				}

				return false;
			},
			onQueryChange: (newQuery) => {
				if (newQuery) {
					setQuery(newQuery);
				}
				else {
					close();
				}
			},
		};

		return () => {
			mentionUserQuery.handler = null;
		};
	});

	useEffect(() => {
		if (!open) {
			return;
		}

		const onEscape = (event: KeyboardEvent) => {
			if (event.key === 'Escape') {
				event.preventDefault();
				event.stopPropagation();

				close();
			}
		};

		const onScroll = (event: Event) => {
			if (!menuRef.current?.contains(event.target as Node)) {
				updateAnchor();
			}
		};

		window.addEventListener('keydown', onEscape, true);
		window.addEventListener('resize', updateAnchor);
		window.addEventListener('scroll', onScroll, true);

		return () => {
			window.removeEventListener('keydown', onEscape, true);
			window.removeEventListener('resize', updateAnchor);
			window.removeEventListener('scroll', onScroll, true);
		};
	}, [close, open]);

	const screenReaderAnnouncerRef = useScreenReaderAnnouncement({
		keywords,
		open,
		resultsCount: mentions.length,
		status,
	});

	const rect = query?.getRect();

	return (
		<>
			<ScreenReaderAnnouncer ref={screenReaderAnnouncerRef} />

			{rect && (
				<>
					<ReactPortal
						ref={anchorRef}
						style={{
							height: rect.height,
							left: rect.left,
							position: 'fixed',
							top: rect.top,
							width: rect.width,
						}}
						wrapper="span"
					/>

					<ClayDropDown.Menu
						active={open}
						alignElementRef={anchorRef}
						className="comments-panel__mention-user-picker"
						onActiveChange={close}
						ref={menuRef}
						suppress={[editableRef, menuRef]}
						triggerRef={editableRef}
					>
						{mentions.length ? (
							<ClayDropDown.ItemList
								aria-label={Liferay.Language.get('mentions')}
								id={listboxId}
								role="listbox"
							>
								{!!users.length && (
									<MentionSection
										header={Liferay.Language.get('people')}
										id={`${id}-people`}
									>
										{users.map((user, index) => (
											<MentionItem
												active={index === activeIndex}
												focus={
													keyboardNavigation &&
													index === activeIndex
												}
												id={`${id}-${index}`}
												key={user.id}
												keywords={results.keywords}
												onMouseEnter={() =>
													setActiveIndex(index)
												}
												onSelect={() =>
													select(mentions[index])
												}
												portraitURL={user.portraitURL}
												stickerDisplayType={getStickerColor(
													user.id
												)}
												stickerShape="circle"
												subtitle={`@${user.screenName}`}
												title={user.name}
											/>
										))}
									</MentionSection>
								)}

								{!!userGroups.length && (
									<MentionSection
										header={Liferay.Language.get('groups')}
										id={`${id}-groups`}
									>
										{userGroups.map((userGroup, index) => {
											const mentionIndex =
												users.length + index;

											return (
												<MentionItem
													active={
														mentionIndex ===
														activeIndex
													}
													focus={
														keyboardNavigation &&
														mentionIndex ===
															activeIndex
													}
													id={`${id}-${mentionIndex}`}
													key={userGroup.id}
													keywords={results.keywords}
													onMouseEnter={() =>
														setActiveIndex(
															mentionIndex
														)
													}
													onSelect={() =>
														select(
															mentions[
																mentionIndex
															]
														)
													}
													stickerDisplayType="outline"
													subtitle={sub(
														userGroup.membersCount ===
															1
															? Liferay.Language.get(
																	'up-to-x-user-will-be-notified'
																)
															: Liferay.Language.get(
																	'up-to-x-users-will-be-notified'
																),
														userGroup.membersCount
													)}
													title={userGroup.name}
												/>
											);
										})}
									</MentionSection>
								)}
							</ClayDropDown.ItemList>
						) : status === 'searching' ? (
							<ClayDropDown.Caption className="font-weight-semi-bold text-dark">
								{Liferay.Language.get('searching')}
							</ClayDropDown.Caption>
						) : (
							<>
								<ClayDropDown.Caption className="font-weight-semi-bold text-dark">
									{getNoMentionsMessage(keywords)}
								</ClayDropDown.Caption>

								<ClayDropDown.Caption>
									{Liferay.Language.get(
										'only-people-and-groups-with-access-to-this-asset-can-be-mentioned'
									)}
								</ClayDropDown.Caption>
							</>
						)}
					</ClayDropDown.Menu>
				</>
			)}
		</>
	);
}

function MentionItem({
	active,
	focus,
	id,
	keywords = '',
	onMouseEnter,
	onSelect,
	portraitURL,
	stickerDisplayType,
	stickerShape,
	subtitle,
	title,
}: {
	active: boolean;
	focus: boolean;
	id: string;
	keywords?: string;
	onMouseEnter: () => void;
	onSelect: () => void;
	portraitURL?: string;
	stickerDisplayType: React.ComponentProps<typeof Sticker>['displayType'];
	stickerShape?: React.ComponentProps<typeof Sticker>['shape'];
	subtitle: string;
	title: string;
}) {
	return (
		<ClayDropDown.Item
			active={active}
			className={classNames({focus})}
			id={id}
			onClick={onSelect}
			onMouseDown={(event) => event.preventDefault()}
			onMouseEnter={onMouseEnter}
			roleItem="option"
		>
			<span className="align-items-center d-flex">
				<Sticker
					aria-hidden="true"
					className="flex-shrink-0 mr-2"
					displayType={stickerDisplayType}
					shape={stickerShape}
					size="sm"
				>
					{portraitURL ? (
						<Sticker.Image alt="" src={portraitURL} />
					) : (
						getInitials(title)
					)}
				</Sticker>

				<span className="autofit-col autofit-col-expand">
					<span className="font-weight-semi-bold text-3 text-dark text-truncate">
						<TextHighlight match={keywords}>{title}</TextHighlight>
					</span>

					<span className="text-2 text-secondary text-truncate">
						{subtitle}
					</span>
				</span>
			</span>
		</ClayDropDown.Item>
	);
}

function MentionSection({
	children,
	header,
	id,
}: {
	children: React.ReactNode;
	header: string;
	id: string;
}) {
	return (
		<>
			<li
				aria-hidden="true"
				className="dropdown-subheader text-secondary"
				id={id}
				role="presentation"
			>
				{header}
			</li>

			<li role="presentation">
				<ul aria-labelledby={id} className="list-unstyled" role="group">
					{children}
				</ul>
			</li>
		</>
	);
}

function getNoMentionsMessage(keywords?: string) {
	if (!keywords) {
		return Liferay.Language.get(
			'no-people-or-groups-are-available-to-mention'
		);
	}

	return sub(Liferay.Language.get('no-people-or-groups-match-x'), keywords);
}

function useScreenReaderAnnouncement({
	keywords,
	open,
	resultsCount,
	status,
}: {
	keywords?: string;
	open: boolean;
	resultsCount: number;
	status: Status;
}) {
	const screenReaderAnnouncerRef = useRef<{
		sendMessage: (message: string) => void;
	}>(null);

	let announcement = '';

	if (open) {
		if (resultsCount) {
			announcement = sub(
				resultsCount === 1
					? Liferay.Language.get('x-result-found')
					: Liferay.Language.get('x-results-found'),
				resultsCount
			);
		}
		else if (status === 'searching') {
			announcement = Liferay.Language.get('searching');
		}
		else {
			announcement = getNoMentionsMessage(keywords);
		}
	}

	useEffect(() => {
		if (announcement) {
			screenReaderAnnouncerRef.current?.sendMessage(announcement);
		}
	}, [announcement]);

	return screenReaderAnnouncerRef;
}
