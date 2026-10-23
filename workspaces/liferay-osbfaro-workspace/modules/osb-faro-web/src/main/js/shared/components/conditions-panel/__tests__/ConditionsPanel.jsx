import ConditionsPanel from '../ConditionsPanel';
import React, {useEffect} from 'react';
import {render, screen} from '@testing-library/react';
import {useAnnounce} from 'shared/components/condition-chip/AnnounceContext';

jest.unmock('react-dom');

const Announcer = ({message}) => {
	const announce = useAnnounce();

	useEffect(() => announce(message), [announce, message]);

	return <span>{message}</span>;
};

describe('ConditionsPanel', () => {
	it('renders the title and its content', () => {
		render(
			<ConditionsPanel
				dragTypes={['stage-condition-chip']}
				title="Lifecycle Conditions"
			>
				<span>Structure</span>
			</ConditionsPanel>
		);

		expect(
			screen.getByRole('heading', {name: /lifecycle conditions/i})
		).toBeInTheDocument();
		expect(screen.getByText(/structure/i)).toBeInTheDocument();
	});

	it('announces the messages of its content', () => {
		render(
			<ConditionsPanel
				dragTypes={['stage-condition-chip']}
				title="Lifecycle Conditions"
			>
				<Announcer message="Stage 1 placed on 2 of 3" />
			</ConditionsPanel>
		);

		expect(screen.getByRole('status')).toHaveTextContent(
			/stage 1 placed/i
		);
	});
});
