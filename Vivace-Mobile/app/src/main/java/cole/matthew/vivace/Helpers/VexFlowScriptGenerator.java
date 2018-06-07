package cole.matthew.vivace.Helpers;

import org.jetbrains.annotations.Contract;

import cole.matthew.vivace.Models.Measure;
import cole.matthew.vivace.Models.Note;

public class VexFlowScriptGenerator
{
    private static VexFlowScriptGenerator ourInstance;

    @Contract(pure = true)
    public static VexFlowScriptGenerator getInstance()
    {
        if (ourInstance == null)
            ourInstance = new VexFlowScriptGenerator();

        return ourInstance;
    }

    private VexFlowScriptGenerator()
    { }

    public String clearScore()
    {
        return "(function() { " +
                   "document.getElementById(\"boo\").innerHTML = \"\"; " +
               "})();";
    }

    public String addMeasureStave(Measure measure)
    {
        StringBuilder stringBuilder = new StringBuilder("(function() {")
                .append("let div = document.getElementById('boo');")
                .append("let renderer = new Vex.Flow.Renderer(div, Vex.Flow.Renderer.Backends.SVG);")
                .append("let context = renderer.getContext();")
                .append("renderer.resize(550, 175);")
                .append("context.setFont(\"Arial\", 10, \"\").setBackgroundFillStyle(\"#eed\");")
                .append("TIME4_4 = { num_beats: ").append(measure.getTimeSignature().getBeatsPerMeasure())
                .append(", beat_value: ").append(measure.getTimeSignature().getBeatUnit())
                .append(", resolution: Vex.Flow.RESOLUTION };")
                .append("let notes = [");

        for (int index = 0; index < measure.getNotes().size(); ++index)
        {
            Note note = measure.getNotes().get(index);
            stringBuilder.append("new Vex.Flow.StaveNote({ keys: [\"")
                         .append(note.getVexFlowKey()).append("\"], auto_stem: true, duration: \"")
                         .append(note.getDurationVexFlowString()).append("\"})");

            if (note.getPitch().contains("b"))
                stringBuilder.append(".addAccidental(0, new Vex.Flow.Accidental('b'))");
            else if (note.getPitch().contains("#"))
                stringBuilder.append(".addAccidental(0, new Vex.Flow.Accidental('#'))");

            if (note.getDurationVexFlowString().matches(".d"))
                stringBuilder.append(".addDotToAll()");
            else if (note.getDurationVexFlowString().matches(".dd"))
                stringBuilder.append(".addDotToAll().addDotToAll()");

            if (index < measure.getNotes().size() - 1)
                stringBuilder.append(", ");
        }

        stringBuilder.append("];let beams = Vex.Flow.Beam.generateBeams(notes);")
                     .append("let voice = new Vex.Flow.Voice(TIME4_4).setStrict(false);")
                     .append("voice.addTickables(notes);")
                     .append("let formatter = new Vex.Flow.Formatter().joinVoices([voice]);")
                     .append("formatter.preCalculateMinTotalWidth([voice]);")
                     .append("let stave = new Vex.Flow.Stave(10, 40, formatter.getMinTotalWidth() + 100);")
                     .append("stave.setContext(context).addClef('treble').addTimeSignature('4/4').draw();")
                     .append("formatter.formatToStave([voice], stave);")
                     .append("voice.draw(context, stave);")
                     .append("beams.forEach(function(beam) { beam.setContext(context).draw(); });")
                     .append("})();");

        return stringBuilder.toString();
    }

//                String javaScript = "(function() {" +
//                                        "let VF = Vex.Flow;" +
//                                        "let vf = new VF.Factory({" +
//                                            "renderer: { elementId: 'boo', width: 550, height: 300 }" +
//                                        "});" +
//                                        "let score = vf.EasyScore();" +
//                                        "let system = vf.System();" +
//                                        "system.addStave({" +
//                                            "voices: [" +
//                                                "score.voice(score.notes('C#5/q, B4, A4, G#4', {stem: 'up'}))," +
//                                                "score.voice(score.notes('C#4/h, C#4', {stem: 'down'}))" +
//                                            "]" +
//                                        "}).addClef('treble').addTimeSignature('4/4');" +
//                                        "system.addStave({" +
//                                            "voices: [" +
//                                                "score.voice(score.notes('C4/q, B4, Eb5, G5'))" +
//                                            "]" +
//                                        "}).addClef('treble').addTimeSignature('4/4');" +
//                                        "vf.draw();" +
//                                    "})();";
//                _scoreUI.evaluateJavascript(javaScript, null);
}
