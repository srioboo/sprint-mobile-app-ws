<script setup lang="ts">
import { ref } from "vue";

defineProps<{ msg: string }>();

// variable de test de aplicacion
const count = ref(0);

// variables para obtener el resultado
const resultado = ref(null);
const error = ref(null);

// MOCKUP de datos basicos - TODO: modificar
var datos = { email: "test@test.com", password: "12345678" };

// constante para obtener el token
const token = ref(null);

// funcion para login
const login = async () => {
  try {
    //const response =
    await fetch("http://localhost:8080/mobile-app-ws/users/login", {
      method: "POST",
      cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
      headers: {
        "Content-type": "application/json", // necesaria para obtener correctamente los datos
      },
      body: JSON.stringify(datos),
    }).then((resp) => {
      console.log("aqui resopla", resp);
      resultado.value = resp;
      resp.headers.forEach(function (val, key) {
        // se obtiene la autorizacion
        if (key == "authorization") {
          token.value = val;
        }
      });
    });
  } catch (e) {
    console.log(e);
  }

  // const response =
  resultado.value = await fetch("http://localhost:8080/mobile-app-ws/users", {
    method: "GET",
    cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
    headers: {
      "Content-Type": "application/json",
      // se incluye el token para autorizacion
      Authorization: token.value,
    },
  }).then((res) => res.json());
};
</script>

<template>
  <form action @submit.prevent="login">
    <p>user: <input v-model="email" placeholder="test@test.com" /></p>
    <p>pass: <input v-model="password" placeholder="12345678" /></p>
    <input type="submit" name="login" value="Login" />
  </form>
  <h1>{{ msg }}</h1>
  <p>Resultado: {{ resultado }}</p>

  <h3>Testing e información</h3>
  <button type="button" @click="count++">count is: {{ count }}</button>

  <p>
    Recommended IDE setup:
    <a href="https://code.visualstudio.com/" target="_blank">VSCode</a>
    +
    <a href="https://github.com/johnsoncodehk/volar" target="_blank">Volar</a>
  </p>

  <p>
    <a href="https://vitejs.dev/guide/features.html" target="_blank"> Vite Docs </a>
    |
    <a href="https://v3.vuejs.org/" target="_blank">Vue 3 Docs</a>
  </p>
</template>

<style scoped>
a {
  color: #42b983;
}

label {
  margin: 0 0.5em;
  font-weight: bold;
}

code {
  background-color: #eee;
  padding: 2px 4px;
  border-radius: 4px;
  color: #304455;
}
</style>
