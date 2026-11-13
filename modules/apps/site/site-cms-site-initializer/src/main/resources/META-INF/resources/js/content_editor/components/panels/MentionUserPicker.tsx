/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
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

import CommentService, {MentionUser} from '../../services/CommentService';
import {
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
		users: MentionUser[];
	}>({users: []});
	const [status, setStatus] = useState<Status>('loading');
	const [, updateAnchor] = useReducer((count: number) => count + 1, 0);

	const anchorRef = useRef<HTMLElement>(null);
	const editableRef = useRef(editor.ui.getEditableElement() ?? null);
	const menuRef = useRef<HTMLDivElement>(null);

	const id = useId();

	const keywords = query?.keywords;
	const {users} = results;

	const open = !!query && (!!users.length || status !== 'loading');

	const activeDescendant =
		open && users.length ? `${id}-${activeIndex}` : null;
	const listboxId = `${id}-listbox`;

	const close = useCallback(() => {
		setKeyboardNavigation(false);
		setQuery(null);
		setResults({users: []});
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
			setResults({keywords, users: data?.users ?? []});
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

	const select = (user: MentionUser) => {
		query?.insert(user);

		close();
	};

	useEffect(() => {
		const mentionUserQuery = editor.plugins.get(MentionUserQuery);

		mentionUserQuery.handler = {
			onKeyDown: (event) => {
				if (!open || !users.length) {
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
						select(users[activeIndex]);
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

		window.addEventListener('keydown', onEscape, true);
		window.addEventListener('resize', updateAnchor);
		window.addEventListener('scroll', updateAnchor, true);

		return () => {
			window.removeEventListener('keydown', onEscape, true);
			window.removeEventListener('resize', updateAnchor);
			window.removeEventListener('scroll', updateAnchor, true);
		};
	}, [close, open]);

	const screenReaderAnnouncerRef = useScreenReaderAnnouncement({
		keywords,
		open,
		resultsCount: users.length,
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
						{users.length ? (
							<>
								<div
									aria-hidden="true"
									className="dropdown-subheader text-secondary"
								>
									{Liferay.Language.get('people')}
								</div>

								<ClayDropDown.ItemList
									aria-label={Liferay.Language.get('people')}
									id={listboxId}
									role="listbox"
								>
									{users.map((user, index) => (
										<ClayDropDown.Item
											active={index === activeIndex}
											className={classNames({
												focus:
													keyboardNavigation &&
													index === activeIndex,
											})}
											id={`${id}-${index}`}
											key={user.id}
											onClick={() => select(user)}
											onMouseDown={(event) =>
												event.preventDefault()
											}
											onMouseEnter={() =>
												setActiveIndex(index)
											}
											roleItem="option"
										>
											<span className="align-items-center d-flex">
												<Sticker
													className={classNames(
														'flex-shrink-0 mr-3',
														{
															[`user-icon-color-${user.id.charCodeAt(0) % 10}`]:
																!user.portraitURL,
														}
													)}
													shape="user-icon"
												>
													{user.portraitURL ? (
														<Sticker.Image
															alt=""
															src={
																user.portraitURL
															}
														/>
													) : (
														<ClayIcon symbol="user" />
													)}
												</Sticker>

												<span className="autofit-col autofit-col-expand">
													<span className="font-weight-semi-bold text-3 text-dark text-truncate">
														{user.name}
													</span>

													<span className="text-2 text-secondary text-truncate">
														{`@${user.screenName}`}
													</span>
												</span>
											</span>
										</ClayDropDown.Item>
									))}
								</ClayDropDown.ItemList>

								<ClayDropDown.Divider />

								<ClayDropDown.Caption>
									{Liferay.Language.get(
										'only-people-with-access-to-this-asset-are-shown'
									)}
								</ClayDropDown.Caption>
							</>
						) : status === 'searching' ? (
							<ClayDropDown.Caption className="font-weight-semi-bold text-dark">
								{Liferay.Language.get('searching')}
							</ClayDropDown.Caption>
						) : (
							<>
								<ClayDropDown.Caption className="font-weight-semi-bold text-dark">
									{getNoPeopleMessage(keywords)}
								</ClayDropDown.Caption>

								<ClayDropDown.Caption>
									{Liferay.Language.get(
										'only-people-with-access-to-this-asset-can-be-mentioned'
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

function getNoPeopleMessage(keywords?: string) {
	if (!keywords) {
		return Liferay.Language.get('no-people-are-available-to-mention');
	}

	return sub(Liferay.Language.get('no-people-match-x'), keywords);
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
			announcement = getNoPeopleMessage(keywords);
		}
	}

	useEffect(() => {
		if (announcement) {
			screenReaderAnnouncerRef.current?.sendMessage(announcement);
		}
	}, [announcement]);

	return screenReaderAnnouncerRef;
}
