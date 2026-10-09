import ClayButton from '@clayui/button';
import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React, {createRef} from 'react';
import {ENTER} from 'shared/util/key-constants';
import {withField} from 'shared/components/form';

interface ITitleEditorProps {
	editable?: boolean;
	inputName: string;
	onBlur?: (event: React.FocusEvent<HTMLInputElement>) => void;
	onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
	placeholder?: string;
	value?: string;
}

export class TitleEditor extends React.Component<
	ITitleEditorProps,
	{editing: boolean}
> {
	static defaultProps = {
		editable: true,
	};

	state = {
		editing: false,
	};

	constructor(props: ITitleEditorProps) {
		super(props);
		this.handleBlur = this.handleBlur.bind(this);
		this.handleEdit = this.handleEdit.bind(this);
		this.handleKeyDown = this.handleKeyDown.bind(this);
		this.handleKeyDownEdit = this.handleKeyDownEdit.bind(this);
		this.editing = this.editing.bind(this);
	}

	private _titleInput = createRef<HTMLInputElement>();

	handleBlur(event: React.FocusEvent<HTMLInputElement>) {
		const {onBlur} = this.props;

		this.setState({editing: false});

		if (onBlur) {
			onBlur(event);
		}
	}

	handleEdit(event: React.MouseEvent | React.KeyboardEvent) {
		event.preventDefault();

		const {editable} = this.props;
		if (editable) {
			this.editing();
		}
	}

	handleKeyDown(event: React.KeyboardEvent<HTMLInputElement>) {
		if (event.keyCode === ENTER && this._titleInput.current) {
			this._titleInput.current.blur();
		}
	}

	handleKeyDownEdit(event: React.KeyboardEvent<HTMLSpanElement>) {
		event.preventDefault();

		if (event.keyCode === ENTER) {
			this.editing();
		}
	}

	editing() {
		this.setState(
			{
				editing: !this.state.editing,
			},
			() => this._titleInput.current?.select()
		);
	}

	render() {
		const {
			props: {editable, inputName, onChange, placeholder, value = ''},
			state: {editing},
		} = this;

		const rootClasses = getCN('title-editor-root', {editing});

		const inputClasses = getCN('title-input', {
			hide: !editing,
		});

		const displayClasses = getCN('title-display', {
			hide: editing,
			'placeholder-display': !value,
		});

		return (
			<div className={rootClasses}>
				<input
					className={inputClasses}
					name={inputName}
					onBlur={this.handleBlur}
					onChange={onChange}
					onKeyDown={this.handleKeyDown}
					placeholder={placeholder}
					ref={this._titleInput}
					required
					type="text"
					value={value}
				/>

				<div className={displayClasses}>
					<span
						className="title-value"
						onClick={this.handleEdit}
						onKeyDown={this.handleKeyDownEdit}
						role="button"
						tabIndex={0}
					>
						{value || placeholder}
					</span>

					{editable && (
						<ClayButton
							aria-label={Liferay.Language.get('edit')}
							borderless
							className="button-root"
							displayType="unstyled"
							onClick={this.handleEdit}
							outline
							size="sm"
						>
							<ClayIcon className="icon-root" symbol="pencil" />
						</ClayButton>
					)}
				</div>
			</div>
		);
	}
}

export default withField(
	({field: {name, ...otherFields}, ...otherProps}: any) => {
		const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
			const {
				form: {setFieldValue},
			} = otherProps;

			setFieldValue(name, event.target.value);
		};

		return (
			<TitleEditor
				{...otherFields}
				{...otherProps}
				inputName={name}
				onChange={handleChange}
			/>
		);
	}
);
