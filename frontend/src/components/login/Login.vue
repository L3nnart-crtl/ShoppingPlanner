<template>
  <div class="auth-container">
    <div class="form-toggle">
      <button
          :class="{ active: isLogin }"
          @click="isLogin = true"
      >
        Login
      </button>
      <button
          :class="{ active: !isLogin }"
          @click="isLogin = false"
      >
        Register
      </button>
    </div>

    <form v-if="isLogin" @submit.prevent="loginUser">
      <h2>Login</h2>
      <div class="input-group">
        <label for="login-username">Username</label>
        <input
            type="text"
            id="login-username"
            v-model="username"
            required
        />
      </div>
      <div class="input-group">
        <label for="login-password">Password</label>
        <input
            type="password"
            id="login-password"
            v-model="password"
            required
        />
      </div>
      <button type="submit">Login</button>
      <div v-if="error" class="error-message">{{ error }}</div>
    </form>

    <form v-else @submit.prevent="registerUser">
      <h2>Register</h2>
      <div class="input-group">
        <label for="register-username">Username</label>
        <input
            type="text"
            id="register-username"
            v-model="username"
            required
        />
      </div>
      <div class="input-group">
        <label for="register-password">Password</label>
        <input
            type="password"
            id="register-password"
            v-model="password"
            required
        />
      </div>
      <button type="submit">Register</button>
      <div v-if="error" class="error-message">{{ error }}</div>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLogin: true,
      username: '',
      password: '',
      error: '',
    };
  },
  methods: {
    async loginUser() {
      try {
        const response = await this.$axios.post('/auth/login', {
          username: this.username,
          password: this.password,
        });
        if (response.status === 200) {
          this.$router.push('/home');
        } else {
          this.error = 'Login failed. Please check your username and password!';
        }
      } catch {
        this.error = 'Login failed. Please check your username and password!';
      }
    },
    async registerUser() {
      try {
        const response = await this.$axios.post('/auth/register', {
          username: this.username,
          password: this.password,
        });
        if (response.status === 200) {
          alert('Registration successful! You can now log in.');
          this.isLogin = true;
          this.username = '';
          this.password = '';
        } else {
          this.error = response.data || 'Registration failed.';
        }
      } catch (error) {
        this.error = error.response?.data || 'Registration failed.';
      }
    },
  },
};
</script>

<style scoped>
.auth-container {
  max-width: 400px;
  margin: 2rem auto;
  padding: 1.5rem;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  background: #f9f9f9;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.form-toggle {
  display: flex;
  justify-content: space-around;
  margin-bottom: 1.5rem;
}

.form-toggle button {
  padding: 10px 20px;
  border: none;
  background: #e0e0e0;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.form-toggle button.active {
  background: #007BFF;
  color: #fff;
}

.form-toggle button:hover {
  background: #0056b3;
  color: #fff;
}

h2 {
  margin-bottom: 1rem;
  font-size: 1.5rem;
  color: #333;
}

.input-group {
  margin-bottom: 1rem;
  text-align: left;
}

.input-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
  color: #555;
}

.input-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1rem;
  box-sizing: border-box;
}

button[type='submit'] {
  width: 100%;
  padding: 12px;
  border: none;
  background: #28a745;
  color: white;
  font-size: 1rem;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s ease;
}

button[type='submit']:hover {
  background: #218838;
}

.error-message {
  margin-top: 0.5rem;
  color: #dc3545;
  font-size: 0.875rem;
}
</style>
