package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.graalvm.polyglot.*;

public class PythonInterop {
    public String getPositions() {
        try (Context ctx = Context.newBuilder("python")
                .option("python.PythonPath", "packages/Lib/site-packages")
                .allowAllAccess(true)
                .build()) {
            Value result = ctx.eval("python", """
                    import json
                    import requests
                    from bs4 import BeautifulSoup
                    
                    base_urls = ["https://commerzbank-poland.breezy.hr", "https://voyagu.breezy.hr"
                    , "https://upstars.breezy.hr/", "https://patrianna.breezy.hr/", "https://medialicious.breezy.hr/"]

                    type_mapping = {
                        "%LABEL_POSITION_TYPE_FULL_TIME%": "Full Time",
                        "%LABEL_POSITION_TYPE_OTHER%": "Other",
                        "%LABEL_POSITION_TYPE_PART_TIME%": "Part Time",
                        "%LABEL_POSITION_TYPE_CONTRACT%" : "Contract"
                    }

                    def get_info(class_name, soup):
                        li_element = soup.find("li", class_=class_name)
                        if li_element:
                            return li_element.find("span").text.strip()
                        return "Not Found"

                    def get_positions_json():
                        job_list = []
                        for base_url in base_urls:
                            response = requests.get(base_url)
                            soup = BeautifulSoup(response.text, "html.parser")
                            position_details = soup.find_all("li", class_="position-details")

                            for position_detail in position_details:
                                position_link = position_detail.find("a").attrs["href"]
                                position_url = base_url + position_link

                                response = requests.get(position_url)
                                soup = BeautifulSoup(response.text, "html.parser")

                                if response.status_code == 200:
                                    title = soup.find("h1").text.strip() if soup.find("h1") else "Not Found"

                                    job_info = {
                                        "Title": title,
                                        "Location": get_info("location", soup),
                                        "Type": type_mapping.get(get_info("type", soup), get_info("type", soup)),
                                        "Department": get_info("department", soup)
                                    }
                                    job_list.append(job_info)

                        return json.dumps(job_list)

                    get_positions_json()
                    """);

            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(result.asString(), Object.class);
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            return writer.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
