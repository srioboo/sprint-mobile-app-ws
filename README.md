# sprint-mobile-app-ws

Código del curso de sprint udemy

## Ejecutar la aplicación

1. Ir al directorio de la aplicación
2. ejecutar "mvn install"
3. ejecutar "mvn spring-boot:run"

## Messajes de commit semáticos (Semantic Commit Messages)

Como añadir mensajes de commit que sean útiles.

Formato: `<tipo>(<ambito>): <resumen>`

`<ambito>` es opcional

### Ejemplo

```
feat: add hat wobble
^--^  ^------------^
|     |
|     +-> Resumen en presente.
|
+-------> Tipo: chore, docs, feat, fix, refactor, style, or test.
```

Ejemplos y explicación de tipos:

- `feat`: (nueva característica para el usuario, no para scripts)
- `fix`: (correción de error para el usuario, no para corregir un script)
- `docs`: (cambios a la documentación)
- `style`: (formateo, punto y comas olvidados, etc; sin cambios en producción)
- `refactor`: (refactoriazación de código de producción, ej. renombrado de  variable)
- `test`: (añadir testos olvidados, refactorizar test; sin cambios en el código de producción)
- `chore`: (actualización de tareas de grunt, etc; sin cambios en el código de producción)

Referencias:

- https://www.conventionalcommits.org/
- https://seesparkbox.com/foundry/semantic_commit_messages
- http://karma-runner.github.io/1.0/dev/git-commit-msg.html

