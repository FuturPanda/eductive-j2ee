# Lancement du projet

## Avec taskfile : 
[TaskFile](https://taskfile.dev/) est une alternative moderne a Make

Dans la racine du projet : 
```
task jakarta
# ou task springboot
```

## Sans :

```
cd tp1-springboot && mvn spring-boot:run
# cd tp1-jakarta && mvn clean package wildfly:run
```

## Points Clés Appris
[3-5 points essentiels que vous avez compris]
- J'avais toujours exécuté Spring dans IntelliJ, qui a tout un set d'outils pour faciliter le run de spring. Donc j'ai essayé de vraiment passer par un ide qui n'a pas d'outils spécifique pour run java.
- Dijkstra a inventé l'architecture en couche ????? Je savais pas https://www.eecs.ucf.edu/~eurip/papers/dijkstra-the68.pdf
- J'avais jamais vraiment fait de switch d'implémentation comme ça, et ça me fait penser qu'on utilise pas assez les interfaces dans nos projets.
