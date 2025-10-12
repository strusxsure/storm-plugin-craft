import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import { useToast } from "@/hooks/use-toast";
import { supabase } from "@/integrations/supabase/client";
import { Loader2, Download, Code, Sparkles, CheckCircle2, FileArchive, Zap, ChevronDown } from "lucide-react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import pluginIcon from "@/assets/plugin-icon.png";
import JSZip from "jszip";

const PluginCreator = () => {
  const [prompt, setPrompt] = useState("");
  const [generatedFiles, setGeneratedFiles] = useState<Record<string, string>>({});
  const [isGenerating, setIsGenerating] = useState(false);
  const [isCompiling, setIsCompiling] = useState(false);
  const [jarBase64, setJarBase64] = useState("");
  const [pluginName, setPluginName] = useState("MyPlugin");
  const [progressLog, setProgressLog] = useState<string[]>([]);
  const [minecraftVersion, setMinecraftVersion] = useState("1.21");
  const [selectedFile, setSelectedFile] = useState<string | null>(null);
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
    setGeneratedFiles({});
    
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

        setGeneratedFiles(data.files);
        setJarBase64("");
        
        // Extract plugin name from plugin.yml
        const pluginYml = data.files["src/main/resources/plugin.yml"];
        if (pluginYml) {
          const nameMatch = pluginYml.match(/name:\s*(\w+)/);
          if (nameMatch) setPluginName(nameMatch[1]);
        }

        // Automatically select the main Java file
        const mainJavaFile = Object.keys(data.files).find(
          (path) => path.startsWith("src/main/java") && path.endsWith(".java")
        );
        setSelectedFile(mainJavaFile || null);
        
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
    if (Object.keys(generatedFiles).length === 0) return;
    
    setIsCompiling(true);
    setProgressLog(prev => [...prev, "🔧 Starting compilation..."]);
    
    try {
      setProgressLog(prev => [...prev, "📦 Compiling with dependencies..."]);
      
      const response = await fetch("https://93b56dfc-72fb-4e3e-a00b-c0497694afd7-00-1ya06760my2a4.pike.replit.dev:8000/", {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          files: generatedFiles,
          pluginName: pluginName,
          minecraftVersion: minecraftVersion,
        }),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.details || data.error || "Unknown error");
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
    if (Object.keys(generatedFiles).length === 0) return;

    try {
      const zip = new JSZip();
      for (const [path, content] of Object.entries(generatedFiles)) {
        zip.file(path, content);
      }
      
      const blob = await zip.generateAsync({ type: "blob" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `${pluginName}-maven-project.zip`;
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
    if (Object.keys(generatedFiles).length === 0) return;

    const mainJavaFile = Object.entries(generatedFiles).find(([path]) => path.endsWith("Main.java"));
    if (!mainJavaFile) {
        toast({ title: "Error", description: "Main.java not found", variant: "destructive" });
        return;
    }

    const blob = new Blob([mainJavaFile[1]], { type: "text/x-java-source" });
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

  const mainJavaFileEntry = Object.entries(generatedFiles).find(
    ([path]) => path.startsWith("src/main/java") && path.endsWith(".java")
  );

  const handleCodeChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    if (mainJavaFileEntry) {
      const [path] = mainJavaFileEntry;
      setGeneratedFiles(prev => ({ ...prev, [path]: e.target.value }));
    }
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
                  <div className="flex justify-between items-center mb-1.5 sm:mb-2">
                    <label className="text-xs sm:text-sm font-medium">Plugin Description</label>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="outline" className="text-xs sm:text-sm px-2 py-1 h-auto">
                          MC {minecraftVersion}
                          <ChevronDown className="w-3 h-3 ml-1" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent>
                        <DropdownMenuItem onSelect={() => setMinecraftVersion("1.21")}>1.21</DropdownMenuItem>
                        <DropdownMenuItem onSelect={() => setMinecraftVersion("1.20.4")}>1.20.4</DropdownMenuItem>
                        <DropdownMenuItem onSelect={() => setMinecraftVersion("1.19.4")}>1.19.4</DropdownMenuItem>
                        <DropdownMenuItem onSelect={() => setMinecraftVersion("1.18.2")}>1.18.2</DropdownMenuItem>
                        <DropdownMenuItem onSelect={() => setMinecraftVersion("1.17.1")}>1.17.1</DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
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
                    disabled={Object.keys(generatedFiles).length === 0 || isCompiling}
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
                    disabled={Object.keys(generatedFiles).length === 0}
                    variant="outline"
                    size="sm"
                    className="border-primary/30 hover:bg-primary/10 text-xs sm:text-sm px-3 py-2"
                  >
                    <FileArchive className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4" />
                    Maven Project
                  </Button>
                  <Button
                    onClick={handleDownloadSource}
                    disabled={Object.keys(generatedFiles).length === 0}
                    variant="outline"
                    size="sm"
                    className="border-primary/30 hover:bg-primary/10 text-xs sm:text-sm px-3 py-2"
                  >
                    <Code className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4" />
                    Source
                  </Button>
                </div>
              </div>

              <div className="grid grid-cols-12 gap-4">
                <div className="col-span-3">
                  <div className="bg-background/50 rounded-lg border border-primary/20 p-2 h-full">
                    <p className="text-xs font-medium mb-2">Files</p>
                    <div className="space-y-1">
                      {Object.keys(generatedFiles).map((path) => (
                        <button
                          key={path}
                          onClick={() => setSelectedFile(path)}
                          className={`w-full text-left text-xs p-1 rounded ${
                            selectedFile === path ? "bg-primary/20" : ""
                          }`}
                        >
                          {path.split("/").pop()}
                        </button>
                      ))}
                    </div>
                  </div>
                </div>
                <div className="col-span-9 relative">
                  <Textarea
                    value={selectedFile ? generatedFiles[selectedFile] : "// Select a file to view its content"}
                    onChange={(e) => {
                      if (selectedFile) {
                        setGeneratedFiles(prev => ({ ...prev, [selectedFile]: e.target.value }));
                      }
                    }}
                    className="p-2 sm:p-3 bg-background/50 rounded-lg border border-primary/20 overflow-auto h-full text-[10px] sm:text-xs leading-tight sm:leading-normal font-mono"
                    readOnly={!selectedFile}
                  />
                  {Object.keys(generatedFiles).length === 0 && (
                    <div className="absolute inset-0 flex items-center justify-center pointer-events-none px-4">
                      <p className="text-muted-foreground text-center text-xs sm:text-sm">
                        Enter a description and click Generate
                      </p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default PluginCreator;
