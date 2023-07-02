FROM eclipse-temurin:17
RUN mkdir /opt/app
COPY ./build/install/app-spring/ /opt/app/
WORKDIR /opt/app
CMD ["bin/app-spring"]