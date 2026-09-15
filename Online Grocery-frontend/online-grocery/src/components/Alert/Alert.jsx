import './Alert.scss';

export default function Alert({ isOpen, message, onClose,type }) {
  if (!isOpen) return null;

  return (
    <div className="custom-alert-overlay">
      <div className="custom-alert-box">
        <h3>{type}</h3>
        <p>{message}</p>
        <button onClick={onClose}>OK</button>
      </div>
    </div>
  );
}
