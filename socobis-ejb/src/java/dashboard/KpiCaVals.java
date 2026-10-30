package dashboard;

import affichage.DashboardGraph;
import affichage.DashboardGraphType;
import affichage.PageRecherche;
import affichage.TableauRecherche;
import bean.ClassMAPTable;

import javax.servlet.http.HttpServletRequest;

public class KpiCaVals extends DashboardGraph {
    public KpiCaVals (ClassMAPTable base, HttpServletRequest req, String[] colValeur, String[] colAffiche, String[] legendes, String dateColonne, DashboardGraphType type) throws Exception {
        super(base, req, colValeur, colAffiche, legendes, dateColonne, type);
    }

    public KpiCaVals(PageRecherche pageRecherche, String[] colValeur, String[] colAffiche, String[] legendes, DashboardGraphType type) {
        super(pageRecherche, colValeur, colAffiche, legendes, type);
    }
    @Override
    public String makeCardHtml() {
        TableauRecherche recap = this.getPageRecherche().getTableauRecap();
        String value1 = recap.getDataDirecte()[0][2];
        String value2 = recap.getDataDirecte()[0][1];
        String value3 = recap.getDataDirecte()[0][3];

        String type = "positive";
        if (value2.contains("-")){
            type = "negative";
        }
        return "<div class=\"col-md-6\" style=\"margin-bottom: 20px;\">\n" +
                "<a href=\"#\" class=\"kpi-card-modern kpi-white-theme\">\n" +
                "<h3 class=\"kpi-title\">"+getTitre()+"</h3>\n" +
                "<div class=\"kpi-data-wrapper\">\n" +
                "<span class=\"kpi-main-val\">"+value1+getUniteAt(0)+"</span>\n" +
                "<span class=\"kpi-badge "+type+" \">\n" +
                "                ↑ "+value2+"% <span class=\"compare-text\"></span>\n" +
                "</span>\n" +
                "</div>\n" +
                "<div class=\"kpi-footer-text\">Total somme <strong>"+value3+" Ar</strong></div>\n" +
                "</a>\n" +
                "</div>";
    }
}
