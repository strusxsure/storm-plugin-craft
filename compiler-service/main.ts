import { serve } from "https://deno.land/std@0.177.0/http/server.ts";
import { Buffer } from "https://deno.land/std@0.177.0/io/buffer.ts";
import { Tar } from "https://deno.land/std@0.177.0/archive/tar.ts";
import { copy } from "https://deno.land/std@0.177.0/streams/copy.ts";
import { btoa } from "https://deno.land/std@0.177.0/encoding/base64.ts";

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
};

// Map simple versions to Spigot API versions
const SPIGOT_VERSIONS: Record<string, string> = {
  "1.21": "1.21-R0.1-SNAPSHOT",
  "1.20.4": "1.20.4-R0.1-SNAPSHOT",
  "1.19.4": "1.19.4-R0.1-SNAPSHOT",
  "1.18.2": "1.18.2-R0.1-SNAPSHOT",
  "1.17.1": "1.17.1-R0.1-SNAPSHOT",
};

function createPomXml(pluginName: string, minecraftVersion: string): string {
  const spigotVersion = SPIGOT_VERSIONS[minecraftVersion] || "1.21-R0.1-SNAPSHOT";

  const groupId = "com.generated.plugin";
  const artifactId = pluginName;

  return `
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>${"$"}{java.version}</source>
                    <target>${"$"}{java.version}</target>
                </configuration>
            </plugin>
        </plugins>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>

    <repositories>
        <repository>
            <id>spigotmc-repo</id>
            <url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot-api</artifactId>
            <version>${spigotVersion}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
  `;
}

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders });
  }

  try {
    const { files, pluginName, minecraftVersion } = await req.json();

    // 1. Create a temporary directory
    const tempDir = await Deno.makeTempDir({ prefix: "plugin-build-" });

    // 2. Write files to the temp directory
    files["pom.xml"] = createPomXml(pluginName, minecraftVersion);

    for (const [path, content] of Object.entries(files)) {
      const fullPath = `${tempDir}/${path}`;
      const dir = fullPath.substring(0, fullPath.lastIndexOf('/'));
      await Deno.mkdir(dir, { recursive: true });
      await Deno.writeTextFile(fullPath, content as string);
    }

    // 3. Run Maven build
    const command = new Deno.Command("mvn", {
      args: ["-B", "clean", "package"],
      cwd: tempDir,
    });

    const { code, stdout, stderr } = await command.output();

    if (code !== 0) {
      const errorOutput = new TextDecoder().decode(stderr);
      console.error("Maven build failed:", errorOutput);
      await Deno.remove(tempDir, { recursive: true }); // Cleanup
      return new Response(
        JSON.stringify({ error: "Compilation failed", details: errorOutput }),
        { status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    // 4. Find and read the JAR file
    const jarPath = `${tempDir}/target/${pluginName}-1.0.0.jar`;
    const jarData = await Deno.readFile(jarPath);

    // 5. Encode to base64
    const base64Jar = btoa(jarData);

    // 6. Cleanup
    await Deno.remove(tempDir, { recursive: true });

    return new Response(
      JSON.stringify({ jarBase64: base64Jar }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    );

  } catch (error) {
    console.error('Error in compile-plugin function:', error);
    const isNotFound = error instanceof Deno.errors.NotFound;
    const errorMessage = isNotFound
      ? "Build environment not found. The server is missing Java/Maven to compile the plugin."
      : (error instanceof Error ? error.message : 'Unknown error occurred');

    return new Response(
      JSON.stringify({ error: "Failed to execute compilation", details: errorMessage }),
      { status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    );
  }
}, { port: 8001 });