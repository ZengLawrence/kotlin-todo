FROM eclipse-temurin:17
RUN mkdir /opt/app
COPY ./build/install/kotlin-todo/ /opt/app/
WORKDIR /opt/app
CMD ["bin/kotlin-todo"]