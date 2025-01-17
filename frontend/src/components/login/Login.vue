<template>
  <div class="auth-container">
    <div class="form-toggle">
      <button @click="isLogin = true">Login</button>
      <button @click="isLogin = false">Register</button>
    </div>

    <form v-if="isLogin" @submit.prevent="loginUser">
      <h2>Login</h2>
      <div>
        <label for="login-username">Username</label>
        <input type="text" id="login-username" v-model="username" required />
      </div>
      <div>
        <label for="login-password">Password</label>
        <input type="password" id="login-password" v-model="password" required />
      </div>
      <button type="submit">Login</button>
      <div v-if="error" class="error-message">{{ error }}</div>
    </form>

    <form v-else @submit.prevent="registerUser">
      <h2>Register</h2>
      <div>
        <label for="register-username">Username</label>
        <input type="text" id="register-username" v-model="username" required />
      </div>
      <div>
        <label for="register-password">Password</label>
        <input type="password" id="register-password" v-model="password" required />
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
  margin: 0 auto;
  padding: 1rem;
  text-align: center;
}

.form-toggle button {
  margin: 0.5rem;
}

.error-message {
  color: red;
  margin-top: 0.5rem;
}
</style>
