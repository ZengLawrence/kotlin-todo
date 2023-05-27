FROM eclipse-temurin:11
RUN mkdir /opt/app
COPY ./build/install/kotlin-todo/ /opt/app/
WORKDIR /opt/app
CMD ["bin/kotlin-todo"]