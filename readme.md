# Creación de contenedores

compilar la applicación

```
gradlew quarkusBuild
```

Crear la imagen

```
cd authors
docker build -t app-authors:1.0.0 .
podman push app-authors:1.0.0 docker.io/jaimesalvador/app-authors
```

```
cd books
docker build -t  app-books:1.0.0 .
podman push app-books:1.0.0 docker.io/jaimesalvador/app-books
```

podman run -d -p 8080:8080 --name books1 
localhost/app-books:1.0.0

podman run -d -p 9090:9090 --name authors1 localhost/app-authors:1.0.0

