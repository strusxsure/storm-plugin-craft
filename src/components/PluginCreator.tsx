import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import { useToast } from "@/hooks/use-toast";
import { supabase } from "@/integrations/supabase/client";
import { Loader2, Download, Code, Sparkles, CheckCircle2 } from "lucide-react";
import pluginIcon from "@/assets/plugin-icon.png";

const PluginCreator = () => {
  const [prompt, setPrompt] = useState("");
  const [generatedCode, setGeneratedCode] = useState("");
  const [isGenerating, setIsGenerating] = useState(false);
  const [isCompiling, setIsCompiling] = useState(false);
  const [isCompiled, setIsCompiled] = useState(false);
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
    setIsCompiled(false);
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
        setProgressLog(prev => [...prev, "✅ Plugin generated successfully!"]);
        toast({
          title: "Success!",
          description: "Your plugin has been generated",
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
    if (!generatedCode) {
      toast({
        title: "Error",
        description: "No code to compile",
        variant: "destructive",
      });
      return;
    }

    setIsCompiling(true);
    setIsCompiled(false);
    
    const compileSteps = [
      "🔨 Initializing compiler...",
      "📝 Validating syntax...",
      "🔍 Checking dependencies...",
      "⚡ Compiling source code...",
      "📦 Packaging JAR file...",
      "✅ Compilation complete!"
    ];

    simulateProgress(compileSteps, () => {
      setIsCompiled(true);
      setIsCompiling(false);
      toast({
        title: "Compiled Successfully!",
        description: "Your plugin is ready to download as .jar",
      });
    });
  };

  const handleDownload = () => {
    if (!generatedCode || !isCompiled) return;

    const blob = new Blob([generatedCode], { type: "application/java-archive" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "plugin.jar";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);

    toast({
      title: "Downloaded!",
      description: "Plugin JAR file has been saved",
    });
  };

  return (
    <div className="min-h-screen bg-gradient-hero py-6 sm:py-8 lg:py-12 px-4 sm:px-6">
      <div className="container mx-auto max-w-7xl">
        {/* Header */}
        <div className="text-center mb-8 sm:mb-10 lg:mb-12 animate-fade-in">
          <img src={pluginIcon} alt="Plugin Icon" className="w-16 h-16 sm:w-20 sm:h-20 mx-auto mb-4 sm:mb-6 animate-glow-pulse" />
          <h2 className="text-3xl sm:text-4xl font-bold mb-3 sm:mb-4 bg-gradient-primary bg-clip-text text-transparent px-2">
            Create Your Plugin
          </h2>
          <p className="text-muted-foreground text-base sm:text-lg px-4">
            Describe what you want, and AI will generate the code
          </p>
        </div>

        <div className="grid lg:grid-cols-2 gap-6 sm:gap-8">
          {/* Input Section */}
          <Card className="p-4 sm:p-6 bg-gradient-card border-primary/20 shadow-elegant">
            <div className="space-y-4 sm:space-y-6">
              <div>
                <label className="text-sm font-medium mb-2 block">Plugin Description</label>
                <Textarea
                  placeholder="Describe the plugin you want to create... (e.g., 'Create a Discord bot plugin that responds to commands and includes moderation features')"
                  value={prompt}
                  onChange={(e) => setPrompt(e.target.value)}
                  className="min-h-[150px] sm:min-h-[200px] lg:min-h-[300px] bg-background/50 border-primary/20 focus:border-primary/50 transition-all text-sm sm:text-base w-full resize-none"
                  rows={6}
                />
              </div>

              {progressLog.length > 0 && (
                <div className="bg-background/50 rounded-lg border border-primary/20 p-3 sm:p-4 max-h-[150px] sm:max-h-[200px] overflow-y-auto">
                  <div className="space-y-1.5 sm:space-y-2">
                    {progressLog.map((log, index) => (
                      <div key={index} className="text-xs sm:text-sm text-muted-foreground flex items-center gap-2 animate-fade-in">
                        <CheckCircle2 className="h-3 w-3 sm:h-4 sm:w-4 text-primary flex-shrink-0" />
                        <span className="break-words">{log}</span>
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
          <Card className="p-4 sm:p-6 bg-gradient-card border-primary/20 shadow-elegant">
            <div className="space-y-4 sm:space-y-6">
              <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                <label className="text-sm font-medium">Generated Code</label>
                <div className="flex gap-2">
                  <Button
                    onClick={handleCompile}
                    disabled={!generatedCode || isCompiling}
                    variant="outline"
                    size="sm"
                    className="border-primary/30 hover:bg-primary/10 flex-1 sm:flex-initial"
                  >
                    {isCompiling ? (
                      <Loader2 className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4 animate-spin" />
                    ) : (
                      <Code className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4" />
                    )}
                    <span className="text-xs sm:text-sm">Compile</span>
                  </Button>
                  <Button
                    onClick={handleDownload}
                    disabled={!generatedCode || !isCompiled}
                    variant="outline"
                    size="sm"
                    className="border-primary/30 hover:bg-primary/10 flex-1 sm:flex-initial disabled:opacity-50"
                  >
                    <Download className="mr-1 sm:mr-2 h-3 w-3 sm:h-4 sm:w-4" />
                    <span className="text-xs sm:text-sm">Download .jar</span>
                  </Button>
                </div>
              </div>

              <div className="relative">
                <pre className="p-3 sm:p-4 bg-background/50 rounded-lg border border-primary/20 overflow-auto max-h-[300px] sm:max-h-[400px] text-xs sm:text-sm">
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
