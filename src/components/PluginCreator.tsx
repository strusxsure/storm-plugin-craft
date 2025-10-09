import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Textarea } from "@/components/ui/textarea";
import { useToast } from "@/hooks/use-toast";
import { supabase } from "@/integrations/supabase/client";
import { Loader2, Download, Code, Sparkles } from "lucide-react";
import pluginIcon from "@/assets/plugin-icon.png";

const PluginCreator = () => {
  const [prompt, setPrompt] = useState("");
  const [generatedCode, setGeneratedCode] = useState("");
  const [isGenerating, setIsGenerating] = useState(false);
  const [isCompiling, setIsCompiling] = useState(false);
  const { toast } = useToast();

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
    try {
      const { data, error } = await supabase.functions.invoke("generate-plugin", {
        body: { prompt },
      });

      if (error) throw error;

      setGeneratedCode(data.code);
      toast({
        title: "Success!",
        description: "Your plugin has been generated",
      });
    } catch (error) {
      console.error("Error generating plugin:", error);
      toast({
        title: "Error",
        description: "Failed to generate plugin. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsGenerating(false);
    }
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
    try {
      // Simulate compilation process
      await new Promise((resolve) => setTimeout(resolve, 2000));
      
      toast({
        title: "Compiled Successfully!",
        description: "Your plugin is ready for deployment",
      });
    } catch (error) {
      console.error("Error compiling plugin:", error);
      toast({
        title: "Error",
        description: "Failed to compile plugin",
        variant: "destructive",
      });
    } finally {
      setIsCompiling(false);
    }
  };

  const handleDownload = () => {
    if (!generatedCode) return;

    const blob = new Blob([generatedCode], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "plugin.js";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);

    toast({
      title: "Downloaded!",
      description: "Plugin file has been saved",
    });
  };

  return (
    <div className="min-h-screen bg-gradient-hero py-12 px-4">
      <div className="container mx-auto max-w-7xl">
        {/* Header */}
        <div className="text-center mb-12 animate-fade-in">
          <img src={pluginIcon} alt="Plugin Icon" className="w-20 h-20 mx-auto mb-6 animate-glow-pulse" />
          <h2 className="text-4xl font-bold mb-4 bg-gradient-primary bg-clip-text text-transparent">
            Create Your Plugin
          </h2>
          <p className="text-muted-foreground text-lg">
            Describe what you want, and AI will generate the code
          </p>
        </div>

        <div className="grid lg:grid-cols-2 gap-8">
          {/* Input Section */}
          <Card className="p-6 bg-gradient-card border-primary/20 shadow-elegant">
            <div className="space-y-6">
              <div>
                <label className="text-sm font-medium mb-2 block">Plugin Description</label>
                <Textarea
                  placeholder="Describe the plugin you want to create... (e.g., 'Create a Discord bot plugin that responds to commands and includes moderation features')"
                  value={prompt}
                  onChange={(e) => setPrompt(e.target.value)}
                  className="min-h-[300px] bg-background/50 border-primary/20 focus:border-primary/50 transition-all"
                />
              </div>

              <Button
                onClick={handleGenerate}
                disabled={isGenerating || !prompt.trim()}
                className="w-full bg-primary hover:bg-primary/90 shadow-glow transition-all"
                size="lg"
              >
                {isGenerating ? (
                  <>
                    <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                    Generating with AI...
                  </>
                ) : (
                  <>
                    <Sparkles className="mr-2 h-5 w-5" />
                    Generate Plugin
                  </>
                )}
              </Button>
            </div>
          </Card>

          {/* Output Section */}
          <Card className="p-6 bg-gradient-card border-primary/20 shadow-elegant">
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <label className="text-sm font-medium">Generated Code</label>
                <div className="flex gap-2">
                  <Button
                    onClick={handleCompile}
                    disabled={!generatedCode || isCompiling}
                    variant="outline"
                    size="sm"
                    className="border-primary/30 hover:bg-primary/10"
                  >
                    {isCompiling ? (
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    ) : (
                      <Code className="mr-2 h-4 w-4" />
                    )}
                    Compile
                  </Button>
                  <Button
                    onClick={handleDownload}
                    disabled={!generatedCode}
                    variant="outline"
                    size="sm"
                    className="border-primary/30 hover:bg-primary/10"
                  >
                    <Download className="mr-2 h-4 w-4" />
                    Download
                  </Button>
                </div>
              </div>

              <div className="relative">
                <pre className="p-4 bg-background/50 rounded-lg border border-primary/20 overflow-auto max-h-[400px] text-sm">
                  <code className="text-foreground">
                    {generatedCode || "// Your generated plugin code will appear here..."}
                  </code>
                </pre>
                {!generatedCode && (
                  <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
                    <p className="text-muted-foreground text-center">
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
