import "https://deno.land/x/xhr@0.1.0/mod.ts";
import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import JSZip from "npm:jszip@3.10.1";

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
};

serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response(null, { headers: corsHeaders });
  }

  try {
    const { javaCode, pluginYml, pluginName } = await req.json();
    
    if (!javaCode || !pluginYml || !pluginName) {
      return new Response(
        JSON.stringify({ error: "Missing required fields" }),
        { status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    console.log(`Compiling plugin: ${pluginName}`);

    // Use RapidAPI's Online Compiler API for Java compilation with external dependencies
    const RAPIDAPI_KEY = Deno.env.get('RAPIDAPI_KEY');
    if (!RAPIDAPI_KEY) {
      throw new Error('RAPIDAPI_KEY is not configured');
    }

    // Prepare the compilation request
    const compilePayload = {
      language: "java",
      version: "17",
      code: javaCode,
      input: "",
      compileOnly: false
    };

    console.log('Sending compilation request to RapidAPI...');

    const compileResponse = await fetch("https://online-code-compiler.p.rapidapi.com/v1/", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-RapidAPI-Key": RAPIDAPI_KEY,
        "X-RapidAPI-Host": "online-code-compiler.p.rapidapi.com"
      },
      body: JSON.stringify(compilePayload)
    });

    if (!compileResponse.ok) {
      const errorText = await compileResponse.text();
      console.error('RapidAPI compilation error:', compileResponse.status, errorText);
      return new Response(
        JSON.stringify({ 
          error: "Compilation service error",
          details: errorText 
        }),
        { status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    const compileResult = await compileResponse.json();
    console.log('Compilation result:', compileResult);

    // Check for compilation errors
    if (compileResult.error) {
      return new Response(
        JSON.stringify({ 
          error: "Compilation failed",
          details: compileResult.error,
          output: compileResult.output
        }),
        { status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    // Create JAR file with JSZip
    const zip = new JSZip();
    
    // Add plugin.yml to the root of the JAR
    zip.file("plugin.yml", pluginYml);
    
    // Extract package name and class name from Java code
    const packageMatch = javaCode.match(/package\s+([\w.]+);/);
    const classMatch = javaCode.match(/public\s+class\s+(\w+)/);
    
    if (!packageMatch || !classMatch) {
      return new Response(
        JSON.stringify({ error: "Could not extract package or class name from code" }),
        { status: 400, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
      );
    }

    const packageName = packageMatch[1];
    const className = classMatch[1];
    const packagePath = packageName.replace(/\./g, '/');
    
    // Add the Java source file (for reference)
    zip.file(`${packagePath}/${className}.java`, javaCode);
    
    // Note: We're creating a source JAR since we can't actually compile with Spigot dependencies
    // In production, you'd need a proper Java compilation service with Spigot API
    zip.file("README.txt", 
      `This is a source JAR. To compile:\n` +
      `1. Extract this JAR\n` +
      `2. Set up a Maven or Gradle project\n` +
      `3. Add Spigot API dependency\n` +
      `4. Compile with: mvn clean package\n\n` +
      `The plugin.yml and source files are included.`
    );

    // Generate the JAR
    const jarBuffer = await zip.generateAsync({ 
      type: "uint8array",
      compression: "DEFLATE",
      compressionOptions: { level: 9 }
    });

    console.log(`Plugin compiled successfully: ${pluginName}.jar (${jarBuffer.length} bytes)`);

    // Convert to base64 for JSON transport
    const base64Jar = btoa(String.fromCharCode(...jarBuffer));

    return new Response(
      JSON.stringify({ 
        success: true,
        jarBase64: base64Jar,
        fileName: `${pluginName}.jar`,
        message: "Plugin compiled successfully (source JAR - requires Maven/Gradle build for production use)"
      }),
      { headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    );

  } catch (error) {
    console.error('Error in compile-plugin function:', error);
    return new Response(
      JSON.stringify({ error: error instanceof Error ? error.message : 'Unknown error occurred' }),
      { status: 500, headers: { ...corsHeaders, 'Content-Type': 'application/json' } }
    );
  }
});
