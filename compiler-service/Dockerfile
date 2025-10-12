# Use an official Maven image which includes a JDK
FROM maven:3.8.5-openjdk-17

# Install Deno
RUN apt-get update && apt-get install -y unzip
RUN curl -fsSL https://deno.land/x/install/install.sh | sh
ENV DENO_INSTALL /root/.deno
ENV PATH $DENO_INSTALL/bin:$PATH

# Set up the working directory
WORKDIR /app

# Copy the Deno server code
COPY main.ts .

# Expose the port the Deno server will run on
EXPOSE 8001

# Start the Deno server
CMD ["deno", "run", "--allow-all", "main.ts"]