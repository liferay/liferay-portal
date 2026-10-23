import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import Input from 'shared/components/Input';
import InputList from 'shared/components/InputList';
import React, {useState} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {COMMA, ENTER} from 'shared/util/key-constants';

//	regex to validate words with accents and avoid special characters

const KEYWORD_VALIDATOR_REGEX = /[\w\u00C0-\u00ff]+/;

interface IInterestTopicsModalProps {
	className: string;
	observer: Observer;
	onClose: () => void;
	onSubmit: (keywords: string[]) => void;
}

const InterestTopicsModal: React.FC<IInterestTopicsModalProps> = ({
	className,
	observer,
	onClose,
	onSubmit,
	...otherProps
}) => {
	const [inputValue, setInputValue] = useState('');
	const [keywords, setKeywords] = useState<string[]>([]);

	const handleSubmit = () => {
		if (
			(keywords.length && !inputValue) ||
			(inputValue && validateKeyword(inputValue))
		) {
			onSubmit(keywords);
		}
	};

	const validateKeyword = (value?: string): boolean =>
		!!value && KEYWORD_VALIDATOR_REGEX.test(value);

	return (
		<ClayModal
			observer={observer}
			{...otherProps}
			className={className}
			size="lg"
		>
			<ClayModal.Header>
				{Liferay.Language.get('insert-keywords')}
			</ClayModal.Header>

			<ClayModal.Body>
				<Input.Group>
					<InputList
						errorMessage={Liferay.Language.get(
							'please-enter-a-valid-keyword'
						)}
						inputValue={inputValue}
						items={keywords}
						keyCodesToSplit={[COMMA, ENTER]}
						onInputChange={(value: string) => {
							setInputValue(value.toLowerCase());
						}}
						onItemsChange={setKeywords}
						placeholder={Liferay.Language.get('enter-keyword')}
						validateOnBlur
						validationFn={validateKeyword}
					/>
				</Input.Group>
				<div className="form-text mb-4">
					{Liferay.Language.get(
						'use-comma-or-enter-to-add-several-keywords'
					)}
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="button-root"
							displayType="secondary"
							onClick={onClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className="button-root"
							disabled={!inputValue && !keywords.length}
							displayType="primary"
							onClick={handleSubmit}
						>
							{Liferay.Language.get('send')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default InterestTopicsModal;
