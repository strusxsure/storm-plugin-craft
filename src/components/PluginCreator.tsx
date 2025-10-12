import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import { useToast } from "@/hooks/use-toast";
import { supabase } from "@/integrations/supabase/client";
import { Loader2, Download, Code, Sparkles, CheckCircle2, FileArchive, Zap } from "lucide-react";
import pluginIcon from "@/assets/plugin-icon.png";
import JSZip from "jszip";

const PluginCreator = () => {
  const [prompt, setPrompt] = useState("");
  const [generatedCode, setGeneratedCode] = useState("");
  const [isGenerating, setIsGenerating] = useState(false);
  const [isCompiling, setIsCompiling] = useState(false);
  const [jarBase64, setJarBase64] = useState("");
  const [pluginName, setPluginName] = useState("MyPlugin");
  const [progressLog, setProgressLog] = useState<string[]>([]);
  const { toast } = useToast();

  const simulateProgress = (steps: string[], callback: () => void) => {
    setProgressLog([]);
    let index = 0;
    const interval = setInterval(() => {
      if (index < steps.length) {
        setProgressLog(prev => [...prev, steps[index]]);
        index++;
      } else {
        clearInterval(interval);
        callback();
      }
    }, 800);
  };

  const handleGenerate = async () => {
    if (!prompt.trim()) {
      toast({
        title: "Error",
        description: "Please describe the plugin you want to create",
        variant: "destructive",
      });
      return;
    }

    setIsGenerating(true);
    setGeneratedCode("");
    
    const generationSteps = [
      "🔍 Analyzing requirements...",
      "📋 Creating project structure...",
      "⚙️ Generating core files...",
      "🔧 Implementing functionality...",
      "📦 Adding dependencies...",
      "✨ Optimizing code...",
      "✅ Finalizing plugin..."
    ];

    simulateProgress(generationSteps, async () => {
      try {
        const { data, error } = await supabase.functions.invoke("generate-plugin", {
          body: { prompt },
        });

        if (error) throw error;

        setGeneratedCode(data.code);
        setJarBase64("");
        
        // Extract plugin name from generated code
        const nameMatch = data.code.match(/name:\s*(\w+)/);
        if (nameMatch) setPluginName(nameMatch[1]);
        
        setProgressLog(prev => [...prev, "✅ Plugin generated! Click 'Compile to JAR' for instant download."]);
        toast({
          title: "Success!",
          description: "Plugin generated! Click Compile to create the JAR file.",
        });
      } catch (error) {
        console.error("Error generating plugin:", error);
        setProgressLog(prev => [...prev, "❌ Error occurred during generation"]);
        toast({
          title: "Error",
          description: "Failed to generate plugin. Please try again.",
          variant: "destructive",
        });
      } finally {
        setIsGenerating(false);
      }
    });
  };

  const handleCompile = async () => {
    if (!generatedCode) return;
    
    setIsCompiling(true);
    setProgressLog(prev => [...prev, "🔧 Starting compilation..."]);
    
    try {
      // Extract plugin.yml from generated code comments
      const pluginYmlMatch = generatedCode.match(/plugin\.yml:?\s*\n([\s\S]*?)(?:\n\n|$)/i);
      let pluginYml = "";
      
      if (pluginYmlMatch) {
        pluginYml = pluginYmlMatch[1].trim();
      } else {
        // Generate basic plugin.yml
        pluginYml = `name: ${pluginName}
version: 1.0.0
main: com.yourplugin.Main
api-version: 1.20
author: AI Generated
description: AI-generated Minecraft plugin`;
      }
      
      setProgressLog(prev => [...prev, "📦 Compiling with dependencies..."]);
      
      const { data, error } = await supabase.functions.invoke("compile-plugin", {
        body: { 
          javaCode: generatedCode,
          pluginYml: pluginYml,
          pluginName: pluginName
        },
      });

      if (error) throw error;

      if (data.error) {
        setProgressLog(prev => [...prev, `❌ Compilation failed: ${data.error}`]);
        toast({
          title: "Compilation Failed",
          description: data.details || "Please check the generated code",
          variant: "destructive",
        });
        return;
      }

      setJarBase64(data.jarBase64);
      setProgressLog(prev => [...prev, "✅ Compilation successful! JAR ready for download."]);
      toast({
        title: "Compiled!",
        description: "Your plugin JAR is ready to download.",
      });
    } catch (error) {
      console.error("Compilation error:", error);
      setProgressLog(prev => [...prev, "❌ Compilation error occurred"]);
      toast({
        title: "Error",
        description: "Failed to compile plugin",
        variant: "destructive",
      });
    } finally {
      setIsCompiling(false);
    }
  };

  const handleDownloadJar = () => {
    if (!jarBase64) return;

    const byteCharacters = atob(jarBase64);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: "application/java-archive" });
    
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `${pluginName}.jar`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);

    toast({
      title: "Downloaded!",
      description: `${pluginName}.jar downloaded successfully`,
    });
  };

  const handleDownloadMaven = async () => {
    if (!generatedCode) return;

    try {
      const zip = new JSZip();
      
      // Create Maven project structure
      zip.file("src/main/java/com/yourplugin/Main.java", generatedCode);
      
      // Create pom.xml with Spigot dependency
      const pomXml = `<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.yourplugin</groupId>
    <artifactId>YourPlugin</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <repositories>
        <repository>
            <id>spigot-repo</id>
            <url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot-api</artifactId>
            <version>1.20.4-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
        </plugins>
    </build>
</project>`;
      
      zip.file("pom.xml", pomXml);
      
      // Create plugin.yml
      const pluginYml = `name: YourPlugin
version: 1.0.0
main: com.yourplugin.Main
api-version: 1.20
author: AI Generated
description: AI-generated Minecraft plugin
commands: {}
permissions: {}`;
      
      zip.file("src/main/resources/plugin.yml", pluginYml);
      
      // Create README
      const readme = `# AI-Generated Minecraft Plugin

## How to Build:

1. Install Maven (https://maven.apache.org/download.cgi)
2. Open terminal in this folder
3. Run: mvn clean package
4. Find your JAR in target/ folder
5. Copy the JAR to your server's plugins/ folder
6. Restart your server

## Requirements:
- Java 17+
- Maven 3.6+
- Spigot/Paper server 1.20+

## Note:
Edit src/main/java/com/yourplugin/Main.java to customize the plugin.
Edit src/main/resources/plugin.yml to change plugin metadata.`;
      
      zip.file("README.md", readme);
      
      const blob = await zip.generateAsync({ type: "blob" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = "minecraft-plugin-maven-project.zip";
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      
      toast({
        title: "Downloaded!",
        description: "Maven project downloaded. Run 'mvn clean package' to build the JAR.",
      });
    } catch (error) {
      console.error("Download error:", error);
      toast({
        title: "Error",
        description: "Failed to create download",
        variant: "destructive",
      });
    }
  };

  const handleDownloadSource = () => {
    if (!generatedCode) return;

    const blob = new Blob([generatedCode], { type: "text/x-java-source" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "Main.java";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);

    toast({ title: "Downloaded!", description: "Java source code saved" });
  };

  return (
    <div className="min-h-screen bg-gradient-hero py-4 sm:py-6 lg:py-8 px-3 sm:px-4">
      <div className="container mx-auto max-w-6xl w-full">
        {/* Header */}
        <div className="text-center mb-4 sm:mb-6 lg:mb-8 animate-fade-in">
          <img src={pluginIcon} alt="Plugin Icon" className="w-12 h-12 sm:w-16 sm:h-16 mx-auto mb-2 sm:mb-4 animate-glow-pulse" />
          <h2 className="text-2xl sm:text-3xl font-bold mb-2 sm:mb-3 bg-gradient-primary bg-clip-text text-transparent px-2">
            Create Your Plugin
          </h2>
          <p className="text-muted-foreground text-sm sm:text-base px-4">
            Describe what you want, and AI will generate the code
          </p>
        </div>

        <div className="grid lg:grid-cols-2 gap-3 sm:gap-4 lg:gap-6">
          {/* Input Section */}
          <Card className="p-3 sm:p-4 lg:p-6 bg-gradient-card border-primary/20 shadow-elegant">
            <div className="space-y-3 sm:space-y-4">
              <div>
                <label className="text-xs sm:text-sm font-medium mb-1.5 sm:mb-2 block">Plugin Description</label>
                <Textarea
                  placeholder="Describe the plugin... (e.g., 'Create a Discord bot plugin with commands and moderation')"
                  value={prompt}
                  onChange={(e) => setPrompt(e.target.value)}
                  className="min-h-[120px] sm:min-h-[160px] lg:min-h-[250px] bg-background/50 border-primary/20 focus:border-primary/50 transition-all text-xs sm:text-sm w-full resize-none"
                  rows={5}
                />
              </div>

              {progressLog.length > 0 && (
                <div className="bg-background/50 rounded-lg border border-primary/20 p-2 sm:p-3 max-h-[120px] sm:max-h-[150px] overflow-y-auto">
                  <div className="space-y-1 sm:space-y-1.5">
                    {progressLog.map((log, index) => (
                      <div key={index} className="text-[10px] sm:text-xs text-muted-foreground flex items-center gap-1.5 sm:gap-2 animate-fade-in">
                        <CheckCircle2 className="h-2.5 w-2.5 sm:h-3 sm:w-3 text-primary flex-shrink-0" />
                        <span className="break-words leading-tight">{log}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              <Button
                onClick={handleGenerate}
                disabled={isGenerating || !prompt.trim()}
                className="w-full bg-primary hover:bg-primary/90 shadow-glow transition-all"
                size="lg"
              >
                {isGenerating ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 sm:h-5 sm:w-5 animate-spin" />
                    <span className="text-sm sm:text-base">Generating with AI...</span>
                  </>
                ) : (
                  <>
                    <Sparkles className="mr-2 h-4 w-4 sm:h-5 sm:w-5" />
                    <span className="text-sm sm:text-base">Generate Plugin</span>
                  </>
                )}
              </Button>
            </div>
          </Card>

          {/* Output Section */}
          <Card className="p-3 sm:p-4 lg:p-6 bg-gradient-card border-primary/20 shadow-elegant">
            <div className="space-y-3 sm:space-y-4">
              <div className="flex flex-col gap-2 sm:gap-3">
                <label className="text-xs sm:text-sm font-medium">Generated Code</label>
                <div className="flex flex-wrap gap-2">
                  <Button
                    onClick={handleCompile}
                    disabled={!generatedCode || isCompiling}
                    className="bg-primary hover:bg-primary/90 text-xs sm:text-sm px-3 py-2 shadow-glow"
                    size="sm"
                  >
                    {isCompiling ? (
                      <>
                        <Loader2 className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4 animate-spin" />
                        Compiling...
                      </>
                    ) : (
                      <>
                        <Zap className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4" />
                        Compile to JAR
                      </>
                    )}
                  </Button>
                  {jarBase64 && (
                    <Button
                      onClick={handleDownloadJar}
                      className="bg-green-600 hover:bg-green-700 text-xs sm:text-sm px-3 py-2"
                      size="sm"
                    >
                      <Download className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4" />
                      Download JAR
                    </Button>
                  )}
                  <Button
                    onClick={handleDownloadMaven}
                    disabled={!generatedCode}
                    variant="outline"
                    size="sm"
                    className="border-primary/30 hover:bg-primary/10 text-xs sm:text-sm px-3 py-2"
                  >
                    <FileArchive className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4" />
                    Maven Project
                  </Button>
                  <Button
                    onClick={handleDownloadSource}
                    disabled={!generatedCode}
                    variant="outline"
                    size="sm"
                    className="border-primary/30 hover:bg-primary/10 text-xs sm:text-sm px-3 py-2"
                  >
                    <Code className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4" />
                    Source
                  </Button>
                </div>
              </div>

              <div className="relative">
                <pre className="p-2 sm:p-3 bg-background/50 rounded-lg border border-primary/20 overflow-auto max-h-[200px] sm:max-h-[300px] lg:max-h-[400px] text-[10px] sm:text-xs leading-tight sm:leading-normal">
                  <code className="text-foreground">
                    {generatedCode || "// Your generated plugin code will appear here..."}
                  </code>
                </pre>
                {!generatedCode && (
                  <div className="absolute inset-0 flex items-center justify-center pointer-events-none px-4">
                    <p className="text-muted-foreground text-center text-xs sm:text-sm">
                      Enter a description and click Generate
                    </p>
                  </div>
                )}
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default PluginCreator;
