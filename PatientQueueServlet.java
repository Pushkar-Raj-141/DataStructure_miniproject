import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/PatientQueueServlet")
public class PatientQueueServlet extends HttpServlet {
    private Queue<Patient> patientQueue = new LinkedList<>();

    // Inner class to represent a patient 
    private class Patient {
        String name;
        String symptoms;
        boolean isEmergency;

        Patient(String name, String symptoms, boolean isEmergency) {
            this.name = name;
            this.symptoms = symptoms;
            this.isEmergency = isEmergency;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Handle adding a new patient
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String name = request.getParameter("name");
            String symptoms = request.getParameter("symptoms");
            boolean isEmergency = Boolean.parseBoolean(request.getParameter("emergency"));

            Patient newPatient = new Patient(name, symptoms, isEmergency);

            // Add patient based on priority
            if (isEmergency) {
                ((LinkedList<Patient>) patientQueue).addFirst(newPatient);
            } else {
                patientQueue.add(newPatient);
            }
        } else if ("serve".equals(action)) {
            // Serve the next patient
            if (!patientQueue.isEmpty()) {
                patientQueue.poll();
            }
        }

        // Return updated queue as JSON
        JSONArray jsonArray = new JSONArray();
        for (Patient patient : patientQueue) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", patient.name);
            jsonObject.put("symptoms", patient.symptoms);
            jsonObject.put("isEmergency", patient.isEmergency);
            jsonArray.put(jsonObject);
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(jsonArray.toString());
        out.flush();
    }
}
