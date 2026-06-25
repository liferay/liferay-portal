/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClaySticker from '@clayui/sticker';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import ClayCard, {
	ClayCardWithHorizontal,
	ClayCardWithInfo,
	ClayCardWithNavigation,
	ClayCardWithUser,
} from '../index';

function ClayCheckboxWithState(props: any) {
	const [value, setValue] = React.useState<boolean>(false);

	return (
		<ClayCheckbox
			checked={value}
			disabled={false}
			onChange={() => setValue((val) => !val)}
		>
			{props.children}
		</ClayCheckbox>
	);
}

describe('ClayCard', () => {
	afterEach(cleanup);

	it('renders a ClayCard as a directory', () => {
		const {container} = render(
			<ClayCard horizontal>
				<ClayCard.Body>
					<ClayCard.Row>
						<div className="flex-col">
							<ClaySticker displayType="secondary" inline>
								<ClayIcon
									spritemap="/path/to/some/resource.svg"
									symbol="folder"
								/>
							</ClaySticker>
						</div>

						<div className="autofit-col autofit-col-expand autofit-col-gutters">
							<div className="autofit-section">
								<ClayCard.Description displayType="title">
									Very Large Folder
								</ClayCard.Description>
							</div>
						</div>
					</ClayCard.Row>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as a selectable folder', () => {
		const {container} = render(
			<ClayCard horizontal selectable>
				<ClayCheckboxWithState>
					<ClayCard.Body>
						<ClayCard.Row>
							<div className="autofit-col">
								<ClaySticker displayType="secondary" inline>
									<ClayIcon
										spritemap="/path/to/some/resource.svg"
										symbol="folder"
									/>
								</ClaySticker>
							</div>

							<div className="autofit-col autofit-col-expand autofit-col-gutters">
								<ClayCard.Description displayType="title">
									Very Large Folder
								</ClayCard.Description>
							</div>
						</ClayCard.Row>
					</ClayCard.Body>
				</ClayCheckboxWithState>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard with an active selected state', () => {
		const {container} = render(
			<ClayCard active selectable>
				<ClayCheckboxWithState>
					<ClayCard.Body>
						<ClayCard.Row>
							<div className="autofit-col">
								<ClaySticker displayType="secondary" inline>
									<ClayIcon
										spritemap="/path/to/some/resource.svg"
										symbol="folder"
									/>
								</ClaySticker>
							</div>

							<div className="autofit-col autofit-col-expand autofit-col-gutters">
								<ClayCard.Description displayType="title">
									Very Large Folder
								</ClayCard.Description>
							</div>
						</ClayCard.Row>
					</ClayCard.Body>
				</ClayCheckboxWithState>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as image card', () => {
		const {container} = render(
			<ClayCard displayType="image">
				<ClayCard.AspectRatio className="card-item-first">
					<img
						alt="thumbnail"
						className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid"
						src="https://via.placeholder.com/256"
					/>

					<ClaySticker displayType="danger" position="bottom-left">
						<ClayIcon
							spritemap="/path/to/some/resource.svg"
							symbol="document-image"
						/>
					</ClaySticker>
				</ClayCard.AspectRatio>

				<ClayCard.Body>
					<ClayCard.Row>
						<div className="autofit-col autofit-col-expand">
							<ClayCard.Description displayType="title">
								thumbnail_coffee.jpg
							</ClayCard.Description>

							<ClayCard.Description displayType="subtitle">
								Author Action
							</ClayCard.Description>

							<ClayCard.Caption>
								<ClayLabel displayType="success">
									Approved
								</ClayLabel>
							</ClayCard.Caption>
						</div>
					</ClayCard.Row>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as a selectable image card', () => {
		const {container} = render(
			<ClayCard displayType="image" selectable>
				<ClayCard.AspectRatio className="card-item-first">
					<ClayCheckboxWithState>
						<ClayCard.AspectRatio>
							<img
								alt="thumbnail"
								className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid"
								src="https://via.placeholder.com/256"
							/>
						</ClayCard.AspectRatio>

						<ClaySticker
							displayType="danger"
							position="bottom-left"
						>
							<ClayIcon
								spritemap="/path/to/some/resource.svg"
								symbol="document-image"
							/>
						</ClaySticker>
					</ClayCheckboxWithState>
				</ClayCard.AspectRatio>

				<ClayCard.Body>
					<ClayCard.Row>
						<div className="autofit-col autofit-col-expand">
							<ClayCard.Description displayType="title">
								thumbnail_coffee.jpg
							</ClayCard.Description>

							<ClayCard.Description displayType="subtitle">
								Author Action
							</ClayCard.Description>

							<ClayCard.Caption>
								<ClayLabel displayType="success">
									Approved
								</ClayLabel>
							</ClayCard.Caption>
						</div>
					</ClayCard.Row>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as a file card', () => {
		const {container} = render(
			<ClayCard displayType="file">
				<ClayCard.AspectRatio className="card-item-first">
					<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
						<ClayIcon
							spritemap="/path/to/some/resource.svg"
							symbol="documents-and-media"
						/>
					</div>

					<ClaySticker displayType="danger" position="bottom-left">
						DOC
					</ClaySticker>
				</ClayCard.AspectRatio>

				<ClayCard.Body>
					<ClayCard.Row>
						<div className="autofit-col autofit-col-expand">
							<section className="autofit-section">
								<ClayCard.Description displayType="title">
									deliverable.doc
								</ClayCard.Description>

								<ClayCard.Description displayType="subtitle">
									Stevie Ray Vaughn
								</ClayCard.Description>

								<ClayCard.Caption>
									<ClayLabel displayType="success">
										Approved
									</ClayLabel>
								</ClayCard.Caption>
							</section>
						</div>
					</ClayCard.Row>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as a selectable file card', () => {
		const {container} = render(
			<ClayCard displayType="file" selectable>
				<ClayCard.AspectRatio className="card-item-first">
					<ClayCheckboxWithState>
						<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
							<ClayIcon
								spritemap="/path/to/some/resource.svg"
								symbol="documents-and-media"
							/>
						</div>

						<ClaySticker
							displayType="danger"
							position="bottom-left"
						>
							DOC
						</ClaySticker>
					</ClayCheckboxWithState>
				</ClayCard.AspectRatio>

				<ClayCard.Body>
					<ClayCard.Row>
						<div className="autofit-col autofit-col-expand">
							<section className="autofit-section">
								<ClayCard.Description displayType="title">
									deliverable.doc
								</ClayCard.Description>

								<ClayCard.Description displayType="subtitle">
									Stevie Ray Vaughn
								</ClayCard.Description>

								<ClayCard.Caption>
									<ClayLabel displayType="success">
										Approved
									</ClayLabel>
								</ClayCard.Caption>
							</section>
						</div>
					</ClayCard.Row>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as template navigation card', () => {
		const {container} = render(
			<ClayCard href="/some/path" interactive>
				<ClayCard.AspectRatio>
					<span className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-flush">
						<img
							alt="portlet image"
							src="/path/to/some/resource.gif"
						/>
					</span>
				</ClayCard.AspectRatio>

				<ClayCard.Body>
					<ClayCard.Description displayType="title">
						Widget Page
					</ClayCard.Description>

					<ClayCard.Description displayType="text">
						Build a page by adding widgets and content.
					</ClayCard.Description>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as a template navigation card truncating text on description', () => {
		const {container} = render(
			<ClayCard href="/some/path" interactive>
				<ClayCard.AspectRatio>
					<span className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-flush">
						<img
							alt="content image"
							src="/path/to/some/resource.gif"
						/>
					</span>
				</ClayCard.AspectRatio>

				<ClayCard.Body>
					<ClayCard.Description displayType="title">
						Content Page
					</ClayCard.Description>

					<ClayCard.Description displayType="text" truncate={false}>
						This is an example of card-type-template using an anchor
						tag.
					</ClayCard.Description>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as template navigation card with icon instead of image', () => {
		const {container} = render(
			<ClayCard href="/some/path" interactive>
				<ClayCard.AspectRatio>
					<span className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-flush">
						<ClayIcon
							spritemap="/path/to/some/resource.svg"
							symbol="page-template"
						/>
					</span>
				</ClayCard.AspectRatio>

				<ClayCard.Body>
					<ClayCard.Description displayType="title">
						Blog
					</ClayCard.Description>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as template navigation card as horizontal card', () => {
		const {container} = render(
			<ClayCard horizontal href="/some/path" interactive>
				<ClayCard.Body>
					<ClayCard.Row>
						<span className="autofit-col">
							<ClaySticker displayType="light" inline>
								<ClayIcon
									spritemap="/path/to/some/resource.svg"
									symbol="page"
								/>
							</ClaySticker>
						</span>

						<span className="autofit-col autofit-col-expand">
							<span className="autofit-section">
								<ClayCard.Description
									displayType="title"
									truncate
								>
									Full Page Application
								</ClayCard.Description>
							</span>
						</span>
					</ClayCard.Row>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as user card', () => {
		const {container} = render(
			<ClayCard displayType="user">
				<ClayCard.AspectRatio className="card-item-first">
					<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
						<ClaySticker
							className="sticker-user-icon"
							displayType="secondary"
							shape="circle"
						>
							<ClayIcon
								spritemap="/path/to/some/resource.svg"
								symbol="user"
							/>
						</ClaySticker>
					</div>
				</ClayCard.AspectRatio>

				<ClayCard.Body>
					<ClayCard.Row>
						<div className="autofit-col autofit-col-expand">
							<ClayCard.Description displayType="title">
								Adélaide
							</ClayCard.Description>

							<ClayCard.Description displayType="subtitle">
								Author Action
							</ClayCard.Description>

							<ClayCard.Caption>
								<ClayLabel displayType="success">
									Approved
								</ClayLabel>
							</ClayCard.Caption>
						</div>
					</ClayCard.Row>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a ClayCard as user selectable card', () => {
		const {container} = render(
			<ClayCard displayType="user" selectable>
				<ClayCard.AspectRatio className="card-item-first">
					<ClayCheckboxWithState>
						<ClayCard.AspectRatio>
							<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
								<ClaySticker
									className="sticker-user-icon"
									displayType="secondary"
									shape="circle"
								>
									<ClayIcon
										spritemap="/path/to/some/resource.svg"
										symbol="user"
									/>
								</ClaySticker>
							</div>
						</ClayCard.AspectRatio>
					</ClayCheckboxWithState>
				</ClayCard.AspectRatio>

				<ClayCard.Body>
					<ClayCard.Row>
						<div className="autofit-col autofit-col-expand">
							<ClayCard.Description displayType="title">
								Adélaide
							</ClayCard.Description>

							<ClayCard.Description displayType="subtitle">
								Author Action
							</ClayCard.Description>

							<ClayCard.Caption>
								<ClayLabel displayType="warning">
									Rejected
								</ClayLabel>
							</ClayCard.Caption>
						</div>
					</ClayCard.Row>
				</ClayCard.Body>
			</ClayCard>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders a group of ClayCards', () => {
		const {container} = render(
			<>
				<ClayCard.Group label="Test Files">
					<ClayCard displayType="file">
						<ClayCard.AspectRatio className="card-item-first">
							<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
								<ClayIcon
									spritemap="/path/to/some/resource.svg"
									symbol="documents-and-media"
								/>
							</div>

							<ClaySticker
								displayType="danger"
								position="bottom-left"
							>
								DOC
							</ClaySticker>
						</ClayCard.AspectRatio>

						<ClayCard.Body>
							<ClayCard.Row>
								<div className="autofit-col autofit-col-expand">
									<section className="autofit-section">
										<ClayCard.Description displayType="title">
											deliverable.doc
										</ClayCard.Description>

										<ClayCard.Description displayType="subtitle">
											Stevie Ray Vaughn
										</ClayCard.Description>

										<ClayCard.Caption>
											<ClayLabel displayType="success">
												Approved
											</ClayLabel>
										</ClayCard.Caption>
									</section>
								</div>
							</ClayCard.Row>
						</ClayCard.Body>
					</ClayCard>

					<ClayCard displayType="file">
						<ClayCard.AspectRatio className="card-item-first">
							<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
								<ClayIcon
									spritemap="/path/to/some/resource.svg"
									symbol="documents-and-media"
								/>
							</div>

							<ClaySticker
								displayType="danger"
								position="bottom-left"
							>
								DOC
							</ClaySticker>
						</ClayCard.AspectRatio>

						<ClayCard.Body>
							<ClayCard.Row>
								<div className="autofit-col autofit-col-expand">
									<section className="autofit-section">
										<ClayCard.Description displayType="title">
											deliverable.doc
										</ClayCard.Description>

										<ClayCard.Description displayType="subtitle">
											Stevie Ray Vaughn
										</ClayCard.Description>

										<ClayCard.Caption>
											<ClayLabel displayType="success">
												Approved
											</ClayLabel>
										</ClayCard.Caption>
									</section>
								</div>
							</ClayCard.Row>
						</ClayCard.Body>
					</ClayCard>
				</ClayCard.Group>
				<ClayCard.Group label="Test Users">
					<ClayCard displayType="user">
						<ClayCard.AspectRatio className="card-item-first">
							<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
								<ClaySticker
									className="sticker-user-icon"
									displayType="secondary"
									shape="circle"
								>
									<ClayIcon
										spritemap="/path/to/some/resource.svg"
										symbol="user"
									/>
								</ClaySticker>
							</div>
						</ClayCard.AspectRatio>

						<ClayCard.Body>
							<ClayCard.Row>
								<div className="autofit-col autofit-col-expand">
									<ClayCard.Description displayType="title">
										Adélaide
									</ClayCard.Description>

									<ClayCard.Description displayType="subtitle">
										Author Action
									</ClayCard.Description>

									<ClayCard.Caption>
										<ClayLabel displayType="success">
											Approved
										</ClayLabel>
									</ClayCard.Caption>
								</div>
							</ClayCard.Row>
						</ClayCard.Body>
					</ClayCard>

					<ClayCard displayType="user">
						<ClayCard.AspectRatio className="card-item-first">
							<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
								<ClaySticker
									className="sticker-user-icon"
									displayType="secondary"
									shape="circle"
								>
									<ClayIcon
										spritemap="/path/to/some/resource.svg"
										symbol="user"
									/>
								</ClaySticker>
							</div>
						</ClayCard.AspectRatio>

						<ClayCard.Body>
							<ClayCard.Row>
								<div className="autofit-col autofit-col-expand">
									<ClayCard.Description displayType="title">
										Adélaide
									</ClayCard.Description>

									<ClayCard.Description displayType="subtitle">
										Author Action
									</ClayCard.Description>

									<ClayCard.Caption>
										<ClayLabel displayType="success">
											Approved
										</ClayLabel>
									</ClayCard.Caption>
								</div>
							</ClayCard.Row>
						</ClayCard.Body>
					</ClayCard>
				</ClayCard.Group>
			</>
		);

		expect(container).toMatchSnapshot();
	});
});

describe('ClayCardWithUser', () => {
	afterEach(cleanup);

	it('renders as selectable', () => {
		const onSelectChangeFn = jest.fn();

		const {container} = render(
			<ClayCardWithUser
				actions={[
					{
						onClick: () => {},
					},
				]}
				href="#"
				name="Foo Bar"
				onSelectChange={onSelectChangeFn}
				selected={false}
				spritemap="/path/to/some/resource.svg"
			/>
		);

		expect(container).toMatchSnapshot();

		fireEvent.click(container.querySelector('label') as HTMLElement, {});

		expect(onSelectChangeFn).toHaveBeenCalled();
	});

	it('renders as disabled', () => {
		const {container} = render(
			<ClayCardWithUser
				disabled
				href="#"
				name="Foo Bar"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with description', () => {
		const {container} = render(
			<ClayCardWithUser
				description="Awesome description of the user"
				href="#"
				name="Foo Bar"
				spritemap="/path/to/some/resource.svg"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with icon', () => {
		const {container} = render(
			<ClayCardWithUser
				href="#"
				name="Foo Bar"
				spritemap="/path/to/some/resource.svg"
				userSymbol="custom-icon"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with image', () => {
		const {container} = render(
			<ClayCardWithUser
				href="#"
				name="Foo Bar"
				spritemap="/path/to/some/resource.svg"
				userImageSrc="/path/to/image.jpg"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with radio button', () => {
		const {container} = render(
			<ClayCardWithUser
				href="#"
				name="Foo Bar"
				onSelectChange={jest.fn()}
				radioProps={{name: 'cards', value: 'radio1'}}
				selectableType="radio"
				spritemap="/path/to/some/resource.svg"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with no truncate text', () => {
		const {container} = render(
			<ClayCardWithUser
				href="#"
				name="Foo Bar"
				selected={false}
				spritemap="/path/to/some/resource.svg"
				truncate={false}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders ClayCardWithNavigation with image', () => {
		const {container} = render(
			<ClayCardWithNavigation
				href="#"
				spritemap="foo/bar"
				title="Layout Page"
			>
				<img alt="portlet image" src="/some/path" />
			</ClayCardWithNavigation>
		);

		expect(container).toMatchSnapshot();
	});

	it('calls onClick callback', () => {
		const onClickFn = jest.fn();

		const {container} = render(
			<ClayCardWithNavigation
				onClick={onClickFn}
				spritemap="foo/bar"
				title="Layout Page"
			>
				<img alt="portlet image" src="/some/path" />
			</ClayCardWithNavigation>
		);

		fireEvent.click(container.querySelector('img') as HTMLElement, {});

		expect(onClickFn).toHaveBeenCalledTimes(1);
	});
});

describe('ClayCardWithHorizontal', () => {
	afterEach(cleanup);

	it('renders as not selectable', () => {
		const {container} = render(
			<ClayCardWithHorizontal
				actions={[
					{
						onClick: () => {},
					},
				]}
				href="#"
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as selectable', () => {
		const onSelectChangeFn = jest.fn();

		const {container} = render(
			<ClayCardWithHorizontal
				href="#"
				onSelectChange={onSelectChangeFn}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();

		fireEvent.click(container.querySelector('label') as HTMLElement, {});

		expect(onSelectChangeFn).toHaveBeenCalled();
	});

	it('renders as disabled', () => {
		const {container} = render(
			<ClayCardWithHorizontal
				disabled
				href="#"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with radio button', () => {
		const {container} = render(
			<ClayCardWithHorizontal
				disabled
				href="#"
				onSelectChange={jest.fn()}
				radioProps={{name: 'cards', value: 'radio1'}}
				selectableType="radio"
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with no truncate text', () => {
		const {container} = render(
			<ClayCardWithUser
				href="#"
				name="Foo Bar"
				selected={false}
				spritemap="/path/to/some/resource.svg"
				truncate={false}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});

describe('ClayCardWithInfo', () => {
	afterEach(cleanup);

	it('renders as not selectable', () => {
		const {container} = render(
			<ClayCardWithInfo
				actions={[
					{
						onClick: () => {},
					},
				]}
				href="#"
				labels={[
					{
						displayType: 'success',
						value: 'Awesome',
					},
				]}
				spritemap="/some/spritemap"
				title="Very Large File"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as selectable', () => {
		const onSelectChangeFn = jest.fn();

		const {container} = render(
			<ClayCardWithInfo
				href="#"
				onSelectChange={onSelectChangeFn}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();

		fireEvent.click(container.querySelector('label') as HTMLElement, {});

		expect(onSelectChangeFn).toHaveBeenCalledTimes(1);
	});

	it('renders as disabled', () => {
		const {container} = render(
			<ClayCardWithInfo
				disabled
				href="#"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with description', () => {
		const {container} = render(
			<ClayCardWithInfo
				description="A cool description"
				href="#"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as file card specifying the displayType', () => {
		const {container} = render(
			<ClayCardWithInfo
				disabled
				displayType="file"
				href="#"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as image card specifying the displayType and no imageProps', () => {
		const {container} = render(
			<ClayCardWithInfo
				disabled
				displayType="image"
				href="#"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as image card specifying the displayType and imageProps', () => {
		const {container} = render(
			<ClayCardWithInfo
				disabled
				displayType="image"
				href="#"
				imgProps="path/to/an/image"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as image card specifying imageProps and not the displayType', () => {
		const {container} = render(
			<ClayCardWithInfo
				disabled
				href="#"
				imgProps="path/to/an/image"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as image card with flush horizontal', () => {
		const {container} = render(
			<ClayCardWithInfo
				flushHorizontal
				href="#"
				imgProps="path/to/an/image"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders as image card with flush vertical', () => {
		const {container} = render(
			<ClayCardWithInfo
				flushVertical
				href="#"
				imgProps="path/to/an/image"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with custom sticker', () => {
		const {container} = render(
			<ClayCardWithInfo
				href="#"
				labels={[
					{
						displayType: 'success',
						value: 'Awesome',
					},
				]}
				spritemap="/some/spritemap"
				stickerProps={{
					className: 'custom-sticker-class',
					displayType: 'danger',
					outside: true,
					position: 'top-right',
					shape: 'circle',
					size: 'xl',
				}}
				title="Very Large File"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with custom sticker with custom Icon', () => {
		const {container} = render(
			<ClayCardWithInfo
				href="#"
				labels={[
					{
						displayType: 'success',
						value: 'Awesome',
					},
				]}
				spritemap="/some/spritemap"
				stickerProps={{
					children: (
						<ClayIcon
							spritemap="/path/to/some/resource.svg"
							symbol="custom-symbol"
						/>
					),
					className: 'custom-sticker-class',
					displayType: 'danger',
					outside: true,
					position: 'top-right',
					shape: 'circle',
					size: 'xl',
				}}
				title="Very Large File"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with no sticker', () => {
		const {container} = render(
			<ClayCardWithInfo
				href="#"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				stickerProps={null}
				title="Foo Bar"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('clicking dropdown item calls callback and not call onSelectChange', () => {
		const onDropDownItemClick = jest.fn();
		const onSelectChangeFn = jest.fn();

		const {container} = render(
			<ClayCardWithInfo
				actions={[
					{
						label: 'clickable',
						onClick: onDropDownItemClick,
					},
				]}
				href="#"
				onSelectChange={onSelectChangeFn}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Selectable File"
			/>
		);

		fireEvent.click(
			container.querySelector('.dropdown-toggle') as HTMLElement,
			{}
		);

		fireEvent.click(
			document.querySelector('.dropdown-item') as HTMLElement,
			{}
		);

		expect(onDropDownItemClick).toHaveBeenCalledTimes(1);
		expect(onSelectChangeFn).not.toHaveBeenCalledTimes(1);
	});

	it('renders with radio button', () => {
		const {container} = render(
			<ClayCardWithInfo
				href="#"
				onSelectChange={jest.fn()}
				radioProps={{name: 'cards', value: 'radio1'}}
				selectableType="radio"
				selected={false}
				spritemap="/path/to/some/resource.svg"
				stickerProps={null}
				title="Radio Card"
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with no truncate text', () => {
		const {container} = render(
			<ClayCardWithInfo
				href="#"
				onSelectChange={jest.fn()}
				selected={false}
				spritemap="/path/to/some/resource.svg"
				title="Foo Bar"
				truncate={false}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
