import iOLogo from '../../assets/img/iO-logo.jpg';
import styles from '../../assets/css/LoginPage.module.css';
import { useNavigate } from 'react-router-dom'; 


import { useState } from 'react';

const LogInPage = () => {
  const [error, setError] = useState('');

  const hardcodedUsername = 'testuser';
  const hardcodedPassword = 'password123';
  const navigate = useNavigate();

  const handleSubmit = (event) => {
    event.preventDefault();



    const form = event.target;
    const username = form.nameOrEmail.value;
    const password = form.password.value;

    if (username === hardcodedUsername && password === hardcodedPassword) {
      setError('');
      alert('Login successful!');
      navigate('/rooms');
    } else {
      setError('Invalid username or password');
    }
  };
  
    return (
      <div className={styles.container}>
        <img src={iOLogo} alt="iO Logo" className={styles.logo} />
        <form className={styles.form} onSubmit={handleSubmit}>          
          <div className={styles.inputGroup}>
            <label htmlFor="nameOrEmail">Name/Email</label>
            <input
              type="text"
              id="nameOrEmail"
              name="nameOrEmail"
              placeholder="Enter your name or email"
              required
            />
          </div>
          <div className={styles.inputGroup}>
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="Enter your password"
              required
            />
          </div>
          {error && <p className={styles.errorMessage}>{error}</p>}
          <button type="submit" className={styles.submitButton}>
            Log In
          </button>
        </form>
      </div>
    );
  };

export default LogInPage; 

