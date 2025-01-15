<template>
  <div class="login-container">
    <form @submit.prevent="loginUser">
      <div>
        <label for="username">Username</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div>
        <label for="password">Password</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button type="submit">Login</button>
    </form>
    <div v-if="error" class="error-message">{{ error }}</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      error: '',
    };
  },
  methods: {
    async loginUser() {
      try {
        const response = await this.$axios.post('http://localhost:8080/api/auth/login', {
          username: this.username,
          password: this.password,
        });

        // Überprüfen, ob die Antwort den String "Login successful" enthält
        if (response.status === 200 && response.data === 'Login successful') {
          // Weiterleiten zur Home-Seite nach erfolgreichem Login
          this.$router.push('/home');
        } else {
          this.error = 'Login failed. Please check your username and password!';
        }
      } catch (error) {
        this.error = 'Login failed. Please check your username and password!';
      }
    },
  },
};
</script>

<style scoped>
/* Optional: Deine Styles für das Login-Formular */
</style>
