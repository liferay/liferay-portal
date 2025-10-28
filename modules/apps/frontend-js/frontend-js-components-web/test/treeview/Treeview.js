/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import Treeview from '../../src/main/resources/META-INF/resources/treeview/Treeview';

const nodes = [
	{
		children: [
			{
				id: '1.1',
				name: 'Pablictor',
			},
			{
				children: [
					{
						id: '1.2.1',
						name: 'Eudaldo',
					},
				],
				id: '1.2',
				name: 'Pabla',
			},
		],
		id: '1',
		name: 'Sandro',
	},
	{
		id: '2',
		name: 'Victor',
	},
	{
		children: [
			{
				id: '3.1',
				name: 'Straight line',
			},
		],
		expanded: true,
		id: '3',
		name: 'Juan',
	},
	{
		children: [
			{
				expanded: true,
				id: '4.1',
				name: 'Victor Son',
			},
		],
		id: '4',
		name: 'Victor Father',
	},
];

describe('Treeview', () => {
	beforeEach(cleanup);

	it('expands a node with corresponding property value', () => {
		const {getByLabelText} = render(<Treeview nodes={nodes} />);

		expect(
			getByLabelText('Collapse Juan').getAttribute('aria-expanded')
		).toBe('true');
	});

	it('expands a node with one of the children expanded', () => {
		const {getByLabelText} = render(<Treeview nodes={nodes} />);

		expect(
			getByLabelText('Collapse Victor Father').getAttribute(
				'aria-expanded'
			)
		).toBe('true');
	});

	it('renders empty', () => {
		const {container} = render(<Treeview nodes={[]} />);
		const treeview = container.querySelector('.lfr-treeview-node-list');
		expect(treeview).toBe(null);
	});

	it('renders a list of nodes', () => {
		const {getByText} = render(<Treeview nodes={nodes} />);

		expect(getByText('Sandro')).toBeVisible();
		expect(getByText('Victor')).toBeVisible();
	});

	it('calls the onSelectedNode callback if nodes are selected', () => {
		const onSelectedNodesChange = jest.fn();
		const {getByText} = render(
			<Treeview
				nodes={nodes}
				onSelectedNodesChange={onSelectedNodesChange}
			/>
		);

		fireEvent.click(getByText('Sandro'));
		fireEvent.click(getByText('Victor'));

		expect(onSelectedNodesChange).toBeCalledWith(new Set(['1', '2']));
	});

	it('marks the initialSelectedNodeIds as selected', () => {
		const {getByLabelText} = render(
			<Treeview initialSelectedNodeIds={['1']} nodes={nodes} />
		);

		expect(getByLabelText('Sandro').checked).toBe(true);
	});

	it('expands nodes if initialSelectedNodeIds are children', () => {
		const {getByLabelText} = render(
			<Treeview initialSelectedNodeIds={['1.2.1']} nodes={nodes} />
		);

		expect(getByLabelText('Eudaldo').checked).toBe(true);
	});

	it('only expands necessary initial nodes', () => {
		const {getByLabelText, queryByLabelText} = render(
			<Treeview initialSelectedNodeIds={['1.1']} nodes={nodes} />
		);

		expect(getByLabelText('Pablictor').checked).toBe(true);
		expect(queryByLabelText('Eudaldo')).not.toBeInTheDocument();
	});

	it('allows selecting several nodes by default', async () => {
		const {getByLabelText} = render(<Treeview nodes={nodes} />);

		fireEvent.click(getByLabelText('Sandro'));
		fireEvent.click(getByLabelText('Victor'));

		expect(getByLabelText('Sandro').checked).toBe(true);
		expect(getByLabelText('Victor').checked).toBe(true);
	});

	it('allows selecting only one node if multiSelection is disabled', async () => {
		const {getByLabelText} = render(
			<Treeview multiSelection={false} nodes={nodes} />
		);

		fireEvent.click(getByLabelText('Sandro'));
		fireEvent.click(getByLabelText('Victor'));

		expect(getByLabelText('Sandro').checked).toBe(false);
		expect(getByLabelText('Victor').checked).toBe(true);
	});

	it('allows expanding nodes on click', () => {
		const {getByLabelText, queryByLabelText} = render(
			<Treeview nodes={nodes} />
		);

		fireEvent.click(getByLabelText('Expand Sandro'));

		expect(queryByLabelText('Pablictor')).toBeInTheDocument();
		expect(queryByLabelText('Pabla')).toBeInTheDocument();
	});

	it('allows collapsing nodes on click', () => {
		const {getByLabelText, queryByLabelText} = render(
			<Treeview initialSelectedNodeIds={['1.1']} nodes={nodes} />
		);

		fireEvent.click(getByLabelText('Collapse Sandro'));

		expect(queryByLabelText('Pablictor')).not.toBeInTheDocument();
		expect(queryByLabelText('Pabla')).not.toBeInTheDocument();
	});

	it('allows specifying a custom NodeComponent', () => {
		const node = {
			icon: 'cog',
			id: '1',
			name: 'Sandro',
			size: 'sm',
		};

		const {getByText} = render(
			<Treeview
				NodeComponent={({node}) => {
					return (
						<button
							data-icon={node.icon}
							data-selected={node.selected}
							data-size={node.size}
							type="button"
						>
							Super {node.name}
						</button>
					);
				}}
				initialSelectedNodeIds={[]}
				nodes={[node]}
			/>
		);

		const button = getByText('Super Sandro');

		fireEvent.click(button);

		expect(button.dataset.selected).toBe('true');
		expect(button.dataset.icon).toBe('cog');
		expect(button.dataset.size).toBe('sm');
	});

	describe('Treeview with inheritedSelection option enabled', () => {
		it('selects children when selecting parent', () => {
			const onSelectedNodesChange = jest.fn();
			const {getByText} = render(
				<Treeview
					inheritSelection
					nodes={nodes}
					onSelectedNodesChange={onSelectedNodesChange}
				/>
			);

			fireEvent.click(getByText('Sandro'));

			expect(onSelectedNodesChange).toBeCalledWith(
				new Set(['1', '1.1', '1.2', '1.2.1'])
			);
		});

		it('enables parent when all of its children are selected', () => {
			const onSelectedNodesChange = jest.fn();
			const {getByText} = render(
				<Treeview
					inheritSelection
					initialSelectedNodeIds={['1.2', '1.2.1']}
					nodes={nodes}
					onSelectedNodesChange={onSelectedNodesChange}
				/>
			);

			fireEvent.click(getByText('Pablictor'));

			expect(onSelectedNodesChange).toBeCalledWith(
				new Set(['1', '1.1', '1.2', '1.2.1'])
			);
		});

		it('disables parent when deselecting on of its children', () => {
			const onSelectedNodesChange = jest.fn();
			const {getByText} = render(
				<Treeview
					inheritSelection
					initialSelectedNodeIds={['1', '1.1', '1.2', '1.2.1']}
					nodes={nodes}
					onSelectedNodesChange={onSelectedNodesChange}
				/>
			);

			fireEvent.click(getByText('Pablictor'));

			expect(onSelectedNodesChange).toBeCalledWith(
				new Set(['1.2', '1.2.1'])
			);
		});
	});

	describe('Treeview with filter prop', () => {
		it('filters the node comparing the node name ignoring case when a string is supplied', () => {
			const nodes = [
				{
					icon: 'cog',
					id: '1',
					name: 'Sandro',
				},
				{
					icon: 'cog',
					id: '2',
					name: 'sandro polo',
				},
				{
					icon: 'cog',
					id: '3',
					name: 'Pablo',
				},
			];

			const {getByText} = render(
				<Treeview filter="Sandro" nodes={nodes} />
			);

			expect(getByText('Sandro')).toBeInTheDocument();
			expect(getByText('sandro polo')).toBeInTheDocument();
		});

		it('uses custom filter function if passed', () => {
			const nodes = [
				{
					icon: 'cog',
					id: '1',
					name: 'Sandro',
				},
				{
					icon: 'cog',
					id: '2',
					name: 'sandro polo',
				},
			];

			const exactMatchFilter = (node) => node.name === 'Sandro';

			const {getByText, queryByText} = render(
				<Treeview filter={exactMatchFilter} nodes={nodes} />
			);

			expect(getByText('Sandro')).toBeInTheDocument();
			expect(queryByText('sandro polo')).not.toBeInTheDocument();
		});
	});

	describe('Treeview controls icon visibility in TreeviewCard', () => {
		it('rendering the icon if present', () => {
			const nodes = [
				{
					icon: 'emoji',
					id: '1',
					name: 'Belt',
				},
				{
					icon: 'react',
					id: '2',
					name: 'Clara',
				},
			];

			const {container} = render(
				<Treeview NodeComponent={Treeview.Card} nodes={nodes} />
			);

			expect(
				container.getElementsByClassName('lexicon-icon').length
			).toBe(2);
			expect(
				container.getElementsByClassName('lexicon-icon-emoji').length
			).toBe(1);
			expect(
				container.getElementsByClassName('lexicon-icon-react').length
			).toBe(1);
		});

		it('not rendering icon if not present or falsy', () => {
			const nodes = [
				{
					id: '1',
					name: 'Belt',
				},
				{
					icon: null,
					id: '2',
					name: 'Clara',
				},
			];

			const {container} = render(
				<Treeview NodeComponent={Treeview.Card} nodes={nodes} />
			);

			expect(
				container.getElementsByClassName('lexicon-icon').length
			).toBe(0);
		});
	});

	describe('Treeview controls icon css class in TreeviewCard', () => {
		it('rendering the icon with the corresponding class if the class property is present', () => {
			const nodes = [
				{
					icon: 'emoji',
					iconCssClass: 'emoji-specific-class',
					id: '1',
					name: 'Belt',
				},
				{
					icon: 'react',
					iconCssClass: 'react-specific-class',
					id: '2',
					name: 'Clara',
				},
			];

			const {container} = render(
				<Treeview NodeComponent={Treeview.Card} nodes={nodes} />
			);

			expect(
				container.getElementsByClassName('emoji-specific-class').length
			).toBe(1);
			expect(
				container.getElementsByClassName('react-specific-class').length
			).toBe(1);
		});

		it('rendering the icon if the class is not present or falsy', () => {
			const nodes = [
				{
					icon: 'emoji',
					iconCssClass: '',
					id: '1',
					name: 'Belt',
				},
				{
					icon: 'react',
					id: '2',
					name: 'Clara',
				},
			];

			const {container} = render(
				<Treeview NodeComponent={Treeview.Card} nodes={nodes} />
			);

			expect(
				container.getElementsByClassName('lexicon-icon').length
			).toBe(2);
		});
	});
});
